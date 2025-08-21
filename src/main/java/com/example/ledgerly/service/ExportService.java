package com.example.ledgerly.service;

import com.example.ledgerly.service.LedgerService.CustomerBalanceSummary;
import com.example.ledgerly.dto.LedgerEntryResponse;
import com.example.ledgerly.entity.Customer;
import com.example.ledgerly.entity.Shop;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.repository.CustomerRepository;
import com.example.ledgerly.repository.LedgerEntryRepository;
import com.example.ledgerly.repository.UserRepository;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ExportService {

    private static final Logger logger = LoggerFactory.getLogger(ExportService.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final LedgerEntryRepository ledgerEntryRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final LedgerService ledgerService;

    @Autowired
    public ExportService(LedgerEntryRepository ledgerEntryRepository,
                        CustomerRepository customerRepository,
                        UserRepository userRepository,
                        LedgerService ledgerService) {
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.ledgerService = ledgerService;
    }

    /**
     * Generate PDF statement for a customer
     */
    public byte[] generateCustomerStatementPDF(Long customerId, LocalDate startDate, LocalDate endDate, String username) {
        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            validateCustomerAccess(customer, username);

            List<LedgerEntryResponse> entries = ledgerEntryRepository
                    .findByCustomerAndDateRange(customerId, startDate, endDate)
                    .stream()
                    .map(entry -> mapToLedgerEntryResponse(entry))
                    .toList();

            CustomerBalanceSummary balanceSummary = ledgerService.getCustomerBalanceSummary(customerId);

            return generatePDFStatement(customer, entries, balanceSummary, startDate, endDate);
        } catch (Exception e) {
            logger.error("Error generating PDF statement for customer {}: {}", customerId, e.getMessage());
            throw new RuntimeException("Failed to generate PDF statement: " + e.getMessage());
        }
    }

    /**
     * Generate CSV statement for a customer
     */
    public byte[] generateCustomerStatementCSV(Long customerId, LocalDate startDate, LocalDate endDate, String username) {
        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            validateCustomerAccess(customer, username);

            List<LedgerEntryResponse> entries = ledgerEntryRepository
                    .findByCustomerAndDateRange(customerId, startDate, endDate)
                    .stream()
                    .map(entry -> mapToLedgerEntryResponse(entry))
                    .toList();

            return generateCSVStatement(customer, entries, startDate, endDate);
        } catch (Exception e) {
            logger.error("Error generating CSV statement for customer {}: {}", customerId, e.getMessage());
            throw new RuntimeException("Failed to generate CSV statement: " + e.getMessage());
        }
    }

    /**
     * Generate PDF transaction history for a customer
     */
    public byte[] generateTransactionHistoryPDF(Long customerId, String username) {
        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            validateCustomerAccess(customer, username);

            List<LedgerEntryResponse> entries = ledgerEntryRepository
                    .findByCustomerIdAndIsActiveTrueOrderByTransactionDateDesc(customerId)
                    .stream()
                    .map(entry -> mapToLedgerEntryResponse(entry))
                    .toList();

            CustomerBalanceSummary balanceSummary = ledgerService.getCustomerBalanceSummary(customerId);

            return generatePDFTransactionHistory(customer, entries, balanceSummary);
        } catch (Exception e) {
            logger.error("Error generating PDF transaction history for customer {}: {}", customerId, e.getMessage());
            throw new RuntimeException("Failed to generate PDF transaction history: " + e.getMessage());
        }
    }

    /**
     * Generate CSV transaction history for a customer
     */
    public byte[] generateTransactionHistoryCSV(Long customerId, String username) {
        try {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            validateCustomerAccess(customer, username);

            List<LedgerEntryResponse> entries = ledgerEntryRepository
                    .findByCustomerIdAndIsActiveTrueOrderByTransactionDateDesc(customerId)
                    .stream()
                    .map(entry -> mapToLedgerEntryResponse(entry))
                    .toList();

            return generateCSVTransactionHistory(customer, entries);
        } catch (Exception e) {
            logger.error("Error generating CSV transaction history for customer {}: {}", customerId, e.getMessage());
            throw new RuntimeException("Failed to generate CSV transaction history: " + e.getMessage());
        }
    }

    /**
     * Validate that the current user has access to the customer's data
     */
    private void validateCustomerAccess(Customer customer, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Staff can only access customers from their assigned shop
        if (currentUser.getRole().name().equals("STAFF")) {
            Optional<Shop> userShop = currentUser.getStaffShopMapping() != null ? 
                    Optional.of(currentUser.getStaffShopMapping().getShop()) : Optional.empty();
            
            if (userShop.isEmpty() || !userShop.get().getId().equals(customer.getShop().getId())) {
                throw new RuntimeException("Access denied: Customer not in your assigned shop");
            }
        }
        // OWNER and ADMIN have access to all customers
    }

    /**
     * Generate PDF statement
     */
    private byte[] generatePDFStatement(Customer customer, List<LedgerEntryResponse> entries, 
                                      CustomerBalanceSummary balanceSummary, LocalDate startDate, LocalDate endDate) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(
                    new com.itextpdf.kernel.pdf.PdfWriter(baos));
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf);

            // Add header
            addPDFHeader(document, customer, startDate, endDate);
            
            // Add customer info
            addPDFCustomerInfo(document, customer, balanceSummary);
            
            // Add transaction summary
            addPDFTransactionSummary(document, entries, startDate, endDate);
            
            // Add detailed transactions
            addPDFTransactionDetails(document, entries);
            
            // Add footer
            addPDFFooter(document);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("Error generating PDF: {}", e.getMessage());
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }

    /**
     * Generate CSV statement
     */
    private byte[] generateCSVStatement(Customer customer, List<LedgerEntryResponse> entries, 
                                      LocalDate startDate, LocalDate endDate) {
        try (StringWriter writer = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(writer)) {

            // Write header
            csvWriter.writeNext(new String[]{
                "Customer Statement",
                customer.getName(),
                "Period: " + startDate.format(DATE_FORMATTER) + " to " + endDate.format(DATE_FORMATTER)
            });
            csvWriter.writeNext(new String[]{""});
            csvWriter.writeNext(new String[]{""});

            // Write customer info
            csvWriter.writeNext(new String[]{"Customer Information"});
            csvWriter.writeNext(new String[]{"Name", customer.getName()});
            csvWriter.writeNext(new String[]{"Email", customer.getEmail() != null ? customer.getEmail() : ""});
            csvWriter.writeNext(new String[]{"Phone", customer.getPhoneNumber() != null ? customer.getPhoneNumber() : ""});
            csvWriter.writeNext(new String[]{"Business", customer.getBusinessName() != null ? customer.getBusinessName() : ""});
            csvWriter.writeNext(new String[]{""});

            // Write transaction summary
            csvWriter.writeNext(new String[]{"Transaction Summary"});
            csvWriter.writeNext(new String[]{"Total Transactions", String.valueOf(entries.size())});
            
            BigDecimal totalCredit = entries.stream()
                    .filter(LedgerEntryResponse::isCredit)
                    .map(LedgerEntryResponse::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            BigDecimal totalDebit = entries.stream()
                    .filter(LedgerEntryResponse::isDebit)
                    .map(LedgerEntryResponse::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            csvWriter.writeNext(new String[]{"Total Credits", totalCredit.toString()});
            csvWriter.writeNext(new String[]{"Total Debits", totalDebit.toString()});
            csvWriter.writeNext(new String[]{"Net Balance", totalCredit.subtract(totalDebit).toString()});
            csvWriter.writeNext(new String[]{""});

            // Write transaction details
            csvWriter.writeNext(new String[]{"Transaction Details"});
            csvWriter.writeNext(new String[]{
                "Date", "Type", "Shop", "Description", "Reference", "Invoice", "Amount", "Balance After"
            });

            for (LedgerEntryResponse entry : entries) {
                csvWriter.writeNext(new String[]{
                    entry.getTransactionDate().format(DATE_FORMATTER),
                    entry.getTransactionType().toString(),
                    entry.getShopName() != null ? entry.getShopName() : "N/A",
                    entry.getDescription() != null ? entry.getDescription() : "",
                    entry.getReferenceNumber() != null ? entry.getReferenceNumber() : "",
                    entry.getInvoiceNumber() != null ? entry.getInvoiceNumber() : "",
                    entry.getAmount().toString(),
                    entry.getBalanceAfterTransaction() != null ? entry.getBalanceAfterTransaction().toString() : ""
                });
            }

            csvWriter.close();
            return writer.toString().getBytes();
        } catch (Exception e) {
            logger.error("Error generating CSV: {}", e.getMessage());
            throw new RuntimeException("Failed to generate CSV: " + e.getMessage());
        }
    }

    /**
     * Generate PDF transaction history
     */
    private byte[] generatePDFTransactionHistory(Customer customer, List<LedgerEntryResponse> entries, 
                                               CustomerBalanceSummary balanceSummary) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(
                    new com.itextpdf.kernel.pdf.PdfWriter(baos));
            com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf);

            // Add header
            addPDFTransactionHistoryHeader(document, customer);
            
            // Add customer info
            addPDFCustomerInfo(document, customer, balanceSummary);
            
            // Add complete transaction history
            addPDFTransactionDetails(document, entries);
            
            // Add footer
            addPDFFooter(document);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("Error generating PDF transaction history: {}", e.getMessage());
            throw new RuntimeException("Failed to generate PDF transaction history: " + e.getMessage());
        }
    }

    /**
     * Generate CSV transaction history
     */
    private byte[] generateCSVTransactionHistory(Customer customer, List<LedgerEntryResponse> entries) {
        try (StringWriter writer = new StringWriter();
             CSVWriter csvWriter = new CSVWriter(writer)) {

            // Write header
            csvWriter.writeNext(new String[]{
                "Complete Transaction History",
                customer.getName(),
                "Generated on: " + LocalDate.now().format(DATE_FORMATTER)
            });
            csvWriter.writeNext(new String[]{""});
            csvWriter.writeNext(new String[]{""});

            // Write customer info
            csvWriter.writeNext(new String[]{"Customer Information"});
            csvWriter.writeNext(new String[]{"Name", customer.getName()});
            csvWriter.writeNext(new String[]{"Email", customer.getEmail() != null ? customer.getEmail() : ""});
            csvWriter.writeNext(new String[]{"Phone", customer.getPhoneNumber() != null ? customer.getPhoneNumber() : ""});
            csvWriter.writeNext(new String[]{"Business", customer.getBusinessName() != null ? customer.getBusinessName() : ""});
            csvWriter.writeNext(new String[]{""});

            // Write transaction details
            csvWriter.writeNext(new String[]{"Complete Transaction History"});
            csvWriter.writeNext(new String[]{
                "Date", "Type", "Shop", "Description", "Reference", "Invoice", "Amount", "Balance After", "Reconciled"
            });

            for (LedgerEntryResponse entry : entries) {
                csvWriter.writeNext(new String[]{
                    entry.getTransactionDate().format(DATE_FORMATTER),
                    entry.getTransactionType().toString(),
                    entry.getShopName() != null ? entry.getShopName() : "N/A",
                    entry.getDescription() != null ? entry.getDescription() : "",
                    entry.getReferenceNumber() != null ? entry.getReferenceNumber() : "",
                    entry.getInvoiceNumber() != null ? entry.getInvoiceNumber() : "",
                    entry.getAmount().toString(),
                    entry.getBalanceAfterTransaction() != null ? entry.getBalanceAfterTransaction().toString() : "",
                    entry.isReconciled() ? "Yes" : "No"
                });
            }

            csvWriter.close();
            return writer.toString().getBytes();
        } catch (Exception e) {
            logger.error("Error generating CSV transaction history: {}", e.getMessage());
            throw new RuntimeException("Failed to generate CSV transaction history: " + e.getMessage());
        }
    }

    // Helper methods for PDF generation
    private void addPDFHeader(com.itextpdf.layout.Document document, Customer customer, LocalDate startDate, LocalDate endDate) {
        com.itextpdf.layout.element.Paragraph header = new com.itextpdf.layout.element.Paragraph(
                "CUSTOMER STATEMENT\n" + customer.getName() + "\n" +
                "Period: " + startDate.format(DATE_FORMATTER) + " to " + endDate.format(DATE_FORMATTER));
        header.setFontSize(16);
        document.add(header);
        document.add(new com.itextpdf.layout.element.Paragraph(""));
    }

    private void addPDFTransactionHistoryHeader(com.itextpdf.layout.Document document, Customer customer) {
        com.itextpdf.layout.element.Paragraph header = new com.itextpdf.layout.element.Paragraph(
                "COMPLETE TRANSACTION HISTORY\n" + customer.getName() + "\n" +
                "Generated on: " + LocalDate.now().format(DATE_FORMATTER));
        header.setFontSize(16);
        document.add(header);
        document.add(new com.itextpdf.layout.element.Paragraph(""));
    }

    private void addPDFCustomerInfo(com.itextpdf.layout.Document document, Customer customer, CustomerBalanceSummary balanceSummary) {
        com.itextpdf.layout.element.Paragraph customerInfo = new com.itextpdf.layout.element.Paragraph(
                "Customer Information:\n" +
                "Name: " + customer.getName() + "\n" +
                "Email: " + (customer.getEmail() != null ? customer.getEmail() : "N/A") + "\n" +
                "Phone: " + (customer.getPhoneNumber() != null ? customer.getPhoneNumber() : "N/A") + "\n" +
                "Business: " + (customer.getBusinessName() != null ? customer.getBusinessName() : "N/A") + "\n" +
                "Current Balance: " + balanceSummary.getCurrentBalance() + "\n" +
                "Credit Limit: " + balanceSummary.getCreditLimit())
                .setFontSize(12);
        document.add(customerInfo);
        document.add(new com.itextpdf.layout.element.Paragraph(""));
    }

    private void addPDFTransactionSummary(com.itextpdf.layout.Document document, List<LedgerEntryResponse> entries, 
                                        LocalDate startDate, LocalDate endDate) {
        BigDecimal totalCredit = entries.stream()
                .filter(LedgerEntryResponse::isCredit)
                .map(LedgerEntryResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalDebit = entries.stream()
                .filter(LedgerEntryResponse::isDebit)
                .map(LedgerEntryResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        com.itextpdf.layout.element.Paragraph summary = new com.itextpdf.layout.element.Paragraph(
                "Transaction Summary (" + startDate.format(DATE_FORMATTER) + " to " + endDate.format(DATE_FORMATTER) + "):\n" +
                "Total Transactions: " + entries.size() + "\n" +
                "Total Credits: " + totalCredit + "\n" +
                "Total Debits: " + totalDebit + "\n" +
                "Net Balance: " + totalCredit.subtract(totalDebit));
        summary.setFontSize(12);
        document.add(summary);
        document.add(new com.itextpdf.layout.element.Paragraph(""));
    }

    private void addPDFTransactionDetails(com.itextpdf.layout.Document document, List<LedgerEntryResponse> entries) {
        com.itextpdf.layout.element.Paragraph detailsHeader = new com.itextpdf.layout.element.Paragraph("Transaction Details:");
        detailsHeader.setFontSize(14);
        document.add(detailsHeader);

        // Create table for transactions
        com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(8);

        // Add table headers
        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Date")));
        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Type")));
        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Shop")));
        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Description")));
        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Reference")));
        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Amount")));
        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Balance")));
        table.addHeaderCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph("Reconciled")));

        // Add transaction rows
        for (LedgerEntryResponse entry : entries) {
            table.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph(entry.getTransactionDate().format(DATE_FORMATTER))));
            table.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph(entry.getTransactionType().toString())));
            table.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph(entry.getShopName() != null ? entry.getShopName() : "N/A")));
            table.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph(entry.getDescription() != null ? entry.getDescription() : "")));
            table.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph(entry.getReferenceNumber() != null ? entry.getReferenceNumber() : "")));
            table.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph(entry.getAmount().toString())));
            table.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph(entry.getBalanceAfterTransaction() != null ? 
                    entry.getBalanceAfterTransaction().toString() : "")));
            table.addCell(new com.itextpdf.layout.element.Cell().add(
                new com.itextpdf.layout.element.Paragraph(entry.isReconciled() ? "Yes" : "No")));
        }

        document.add(table);
        document.add(new com.itextpdf.layout.element.Paragraph(""));
    }

    private void addPDFFooter(com.itextpdf.layout.Document document) {
        com.itextpdf.layout.element.Paragraph footer = new com.itextpdf.layout.element.Paragraph(
                "Generated by Ledgerly System\n" +
                "Generated on: " + LocalDate.now().format(DATE_FORMATTER) + " at " + 
                java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        footer.setFontSize(10);
        document.add(footer);
    }

    /**
     * Helper method to map LedgerEntry to LedgerEntryResponse
     */
    private LedgerEntryResponse mapToLedgerEntryResponse(com.example.ledgerly.entity.LedgerEntry entry) {
        LedgerEntryResponse response = new LedgerEntryResponse();
        response.setId(entry.getId());
        response.setTransactionDate(entry.getTransactionDate());
        response.setTransactionType(entry.getTransactionType());
        response.setAmount(entry.getAmount());
        response.setDescription(entry.getDescription());
        response.setNotes(entry.getNotes());
        response.setReferenceNumber(entry.getReferenceNumber());
        response.setInvoiceNumber(entry.getInvoiceNumber());
        response.setInvoiceDate(entry.getInvoiceDate());
        response.setPaymentMethod(entry.getPaymentMethod());
        response.setBalanceAfterTransaction(entry.getBalanceAfterTransaction());
        response.setReconciled(entry.isReconciled());
        response.setReconciledDate(entry.getReconciledDate());
        response.setActive(entry.isActive());
        response.setCustomerId(entry.getCustomer().getId());
        response.setCustomerName(entry.getCustomer().getName());
        response.setCreatedAt(entry.getCreatedAt());
        response.setUpdatedAt(entry.getUpdatedAt());
        return response;
    }
}
