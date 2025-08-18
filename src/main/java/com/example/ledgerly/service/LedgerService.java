package com.example.ledgerly.service;

import com.example.ledgerly.dto.*;
import com.example.ledgerly.entity.*;
import com.example.ledgerly.exception.BusinessRuleException;
import com.example.ledgerly.repository.CustomerRepository;
import com.example.ledgerly.repository.LedgerEntryRepository;
import com.example.ledgerly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for Ledger entry operations
 */
@Service
@Transactional
public class LedgerService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final BusinessRuleService businessRuleService;
    private final AuditService auditService;

    @Autowired
    public LedgerService(LedgerEntryRepository ledgerEntryRepository,
                        CustomerRepository customerRepository,
                        UserRepository userRepository,
                        FileUploadService fileUploadService,
                        BusinessRuleService businessRuleService,
                        AuditService auditService) {
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.fileUploadService = fileUploadService;
        this.businessRuleService = businessRuleService;
        this.auditService = auditService;
    }

    /**
     * Create a new ledger entry
     */
    public LedgerEntryResponse createLedgerEntry(LedgerEntryCreateRequest request) {
        // Get current user
        User currentUser = getCurrentUser();

        try {
            // Validate customer
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found with id: " + request.getCustomerId()));

            // Create new ledger entry for validation
            LedgerEntry ledgerEntry = new LedgerEntry();
            ledgerEntry.setCustomer(customer);
            ledgerEntry.setTransactionDate(request.getTransactionDate());
            ledgerEntry.setTransactionType(request.getTransactionType());
            ledgerEntry.setAmount(request.getAmount());
            ledgerEntry.setDescription(request.getDescription().trim());
            ledgerEntry.setNotes(request.getNotes());
            ledgerEntry.setReferenceNumber(request.getReferenceNumber());
            ledgerEntry.setInvoiceNumber(request.getInvoiceNumber());
            ledgerEntry.setInvoiceDate(request.getInvoiceDate());
            ledgerEntry.setPaymentMethod(request.getPaymentMethod());
            ledgerEntry.setCreatedBy(currentUser);

            // Apply business rule validations
            businessRuleService.validateLedgerEntryCreation(ledgerEntry, currentUser);

            // Calculate and set balance after transaction
            BigDecimal currentBalance = calculateCurrentBalanceForCustomer(customer.getId());
            BigDecimal newBalance;
            
            if (ledgerEntry.isCredit()) {
                newBalance = currentBalance.add(request.getAmount());
            } else {
                newBalance = currentBalance.subtract(request.getAmount());
            }
            
            ledgerEntry.setBalanceAfterTransaction(newBalance);

            // Create audit snapshot before saving
            Map<String, Object> auditSnapshot = auditService.createAuditSnapshot(ledgerEntry);

            // Save ledger entry
            LedgerEntry savedEntry = ledgerEntryRepository.save(ledgerEntry);

            // Update customer's current balance
            customer.setCurrentBalance(newBalance);
            customerRepository.save(customer);

            // Log successful creation
            auditService.logSuccess("CREATE_LEDGER_ENTRY", "LEDGER_ENTRY", savedEntry.getId(),
                    null, auditSnapshot, 
                    String.format("Created %s entry of %s for customer %s", 
                                request.getTransactionType(), request.getAmount(), customer.getName()),
                    currentUser);

            return convertToResponse(savedEntry);

        } catch (BusinessRuleException e) {
            // Business rule violations are already logged by BusinessRuleService
            throw e;
        } catch (Exception e) {
            // Log general failures
            auditService.logFailure("CREATE_LEDGER_ENTRY", "LEDGER_ENTRY", null,
                    e.getMessage(), 
                    String.format("Failed to create ledger entry for customer %d", request.getCustomerId()),
                    currentUser);
            throw e;
        }
    }

    /**
     * Create ledger entry with file attachments
     */
    public LedgerEntryResponse createLedgerEntryWithAttachments(LedgerEntryCreateRequest request, 
                                                              List<MultipartFile> files, String attachmentDescription) {
        // Create the ledger entry first
        LedgerEntryResponse response = createLedgerEntry(request);

        // Upload files if provided
        if (files != null && !files.isEmpty()) {
            User currentUser = getCurrentUser();
            List<DocumentAttachment> attachments = fileUploadService.uploadMultipleFiles(
                    files, attachmentDescription, currentUser);

            // Link attachments to ledger entry
            LedgerEntry ledgerEntry = ledgerEntryRepository.findById(response.getId())
                    .orElseThrow(() -> new RuntimeException("Ledger entry not found"));

            for (DocumentAttachment attachment : attachments) {
                ledgerEntry.addAttachment(attachment);
            }

            ledgerEntryRepository.save(ledgerEntry);

            // Update response with attachments
            response.setAttachments(attachments.stream()
                    .map(this::convertToAttachmentResponse)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    /**
     * Get ledger entry by ID
     */
    public LedgerEntryResponse getLedgerEntryById(Long id) {
        LedgerEntry ledgerEntry = ledgerEntryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ledger entry not found with id: " + id));
        return convertToResponse(ledgerEntry);
    }

    /**
     * Update an existing ledger entry
     */
    public LedgerEntryResponse updateLedgerEntry(Long id, LedgerEntryUpdateRequest request) {
        User currentUser = getCurrentUser();
        
        try {
            LedgerEntry existingEntry = ledgerEntryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ledger entry not found with id: " + id));

            // Create audit snapshot of old values
            Map<String, Object> oldValues = auditService.createAuditSnapshot(existingEntry);

            // Create updated entry for validation
            LedgerEntry updatedEntry = new LedgerEntry();
            updatedEntry.setAmount(request.getAmount());
            updatedEntry.setTransactionDate(request.getTransactionDate());
            updatedEntry.setTransactionType(request.getTransactionType());

            // Apply business rule validations
            businessRuleService.validateLedgerEntryUpdate(existingEntry, updatedEntry, currentUser);

            // Update fields (only if provided)
            if (request.getTransactionDate() != null) {
                existingEntry.setTransactionDate(request.getTransactionDate());
            }
            if (request.getTransactionType() != null) {
                existingEntry.setTransactionType(request.getTransactionType());
            }
            if (request.getAmount() != null) {
                existingEntry.setAmount(request.getAmount());
            }
            if (request.getDescription() != null) {
                existingEntry.setDescription(request.getDescription().trim());
            }
            if (request.getNotes() != null) {
                existingEntry.setNotes(request.getNotes());
            }
            if (request.getReferenceNumber() != null) {
                existingEntry.setReferenceNumber(request.getReferenceNumber());
            }
            if (request.getInvoiceNumber() != null) {
                existingEntry.setInvoiceNumber(request.getInvoiceNumber());
            }
            if (request.getInvoiceDate() != null) {
                existingEntry.setInvoiceDate(request.getInvoiceDate());
            }
            if (request.getPaymentMethod() != null) {
                existingEntry.setPaymentMethod(request.getPaymentMethod());
            }
            if (request.getIsReconciled() != null) {
                existingEntry.setReconciled(request.getIsReconciled());
                if (request.getIsReconciled()) {
                    existingEntry.setReconciledDate(request.getReconciledDate() != null ? 
                            request.getReconciledDate() : LocalDate.now());
                } else {
                    existingEntry.setReconciledDate(null);
                }
            }
            if (request.getIsActive() != null) {
                existingEntry.setActive(request.getIsActive());
            }

            existingEntry.setUpdatedBy(currentUser);

            // Recalculate balance if amount or type changed
            if (request.getAmount() != null || request.getTransactionType() != null) {
                recalculateCustomerBalance(existingEntry.getCustomer().getId());
            }

            LedgerEntry savedEntry = ledgerEntryRepository.save(existingEntry);

            // Create audit snapshot of new values
            Map<String, Object> newValues = auditService.createAuditSnapshot(savedEntry);

            // Log successful update
            auditService.logSuccess("UPDATE_LEDGER_ENTRY", "LEDGER_ENTRY", savedEntry.getId(),
                    oldValues, newValues,
                    String.format("Updated ledger entry %d for customer %s", 
                                savedEntry.getId(), savedEntry.getCustomer().getName()),
                    currentUser);

            return convertToResponse(savedEntry);

        } catch (BusinessRuleException e) {
            // Business rule violations are already logged by BusinessRuleService
            throw e;
        } catch (Exception e) {
            // Log general failures
            auditService.logFailure("UPDATE_LEDGER_ENTRY", "LEDGER_ENTRY", id,
                    e.getMessage(), 
                    String.format("Failed to update ledger entry %d", id),
                    currentUser);
            throw e;
        }
    }

    /**
     * Delete a ledger entry (soft delete)
     */
    public void deleteLedgerEntry(Long id) {
        User currentUser = getCurrentUser();
        
        try {
            LedgerEntry ledgerEntry = ledgerEntryRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Ledger entry not found with id: " + id));

            // Apply business rule validations for deletion
            businessRuleService.validateLedgerEntryDeletion(ledgerEntry, currentUser);

            // Create audit snapshot before deletion
            Map<String, Object> oldValues = auditService.createAuditSnapshot(ledgerEntry);

            ledgerEntry.setActive(false);
            ledgerEntry.setUpdatedBy(currentUser);
            LedgerEntry deletedEntry = ledgerEntryRepository.save(ledgerEntry);

            // Recalculate customer balance
            recalculateCustomerBalance(ledgerEntry.getCustomer().getId());

            // Log successful deletion
            auditService.logSuccess("DELETE_LEDGER_ENTRY", "LEDGER_ENTRY", deletedEntry.getId(),
                    oldValues, null,
                    String.format("Deleted ledger entry %d for customer %s", 
                                deletedEntry.getId(), deletedEntry.getCustomer().getName()),
                    currentUser);

        } catch (BusinessRuleException e) {
            // Business rule violations are already logged by BusinessRuleService
            throw e;
        } catch (Exception e) {
            // Log general failures
            auditService.logFailure("DELETE_LEDGER_ENTRY", "LEDGER_ENTRY", id,
                    e.getMessage(), 
                    String.format("Failed to delete ledger entry %d", id),
                    currentUser);
            throw e;
        }
    }

    /**
     * Get ledger entries for a customer with pagination
     */
    public Page<LedgerEntryResponse> getLedgerEntriesForCustomer(Long customerId, int page, int size, 
                                                               String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<LedgerEntry> entries = ledgerEntryRepository.findByCustomerIdAndIsActiveTrueOrderByTransactionDateDesc(
                customerId, pageable);
        
        return entries.map(this::convertToResponse);
    }

    /**
     * Search ledger entries by multiple criteria
     */
    public Page<LedgerEntryResponse> searchLedgerEntries(Long customerId, TransactionType transactionType,
                                                       LocalDate startDate, LocalDate endDate,
                                                       BigDecimal minAmount, BigDecimal maxAmount,
                                                       String description, String referenceNumber,
                                                       String invoiceNumber, Boolean isReconciled,
                                                       int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<LedgerEntry> entries = ledgerEntryRepository.searchLedgerEntries(
                customerId, transactionType, startDate, endDate, minAmount, maxAmount,
                description, referenceNumber, invoiceNumber, isReconciled, pageable);
        
        return entries.map(this::convertToResponse);
    }

    /**
     * Get customer balance summary
     */
    public CustomerBalanceSummary getCustomerBalanceSummary(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        BigDecimal totalCredit = ledgerEntryRepository.calculateTotalCreditForCustomer(customerId);
        BigDecimal totalDebit = ledgerEntryRepository.calculateTotalDebitForCustomer(customerId);
        BigDecimal currentBalance = ledgerEntryRepository.calculateCurrentBalanceForCustomer(customerId);

        long totalTransactions = ledgerEntryRepository.countByTransactionTypeAndIsActiveTrue(TransactionType.CREDIT) +
                               ledgerEntryRepository.countByTransactionTypeAndIsActiveTrue(TransactionType.DEBIT);

        return new CustomerBalanceSummary(customer.getId(), customer.getName(), 
                totalCredit, totalDebit, currentBalance, totalTransactions,
                customer.getCreditLimit());
    }

    /**
     * Get unreconciled entries for a customer
     */
    public List<LedgerEntryResponse> getUnreconciledEntries(Long customerId) {
        List<LedgerEntry> entries = ledgerEntryRepository.findByCustomerIdAndIsReconciledFalseAndIsActiveTrueOrderByTransactionDateDesc(customerId);
        return entries.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Add attachment to existing ledger entry
     */
    public LedgerEntryResponse addAttachmentToEntry(Long entryId, MultipartFile file, String description) {
        LedgerEntry ledgerEntry = ledgerEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Ledger entry not found with id: " + entryId));

        User currentUser = getCurrentUser();
        DocumentAttachment attachment = fileUploadService.uploadFile(file, description, currentUser);
        
        ledgerEntry.addAttachment(attachment);
        LedgerEntry savedEntry = ledgerEntryRepository.save(ledgerEntry);
        
        return convertToResponse(savedEntry);
    }

    /**
     * Remove attachment from ledger entry
     */
    public void removeAttachmentFromEntry(Long entryId, Long attachmentId) {
        LedgerEntry ledgerEntry = ledgerEntryRepository.findById(entryId)
                .orElseThrow(() -> new RuntimeException("Ledger entry not found with id: " + entryId));

        DocumentAttachment attachment = fileUploadService.getAttachmentById(attachmentId);
        ledgerEntry.removeAttachment(attachment);
        
        ledgerEntryRepository.save(ledgerEntry);
        fileUploadService.deleteAttachment(attachmentId);
    }

    /**
     * Calculate current balance for a customer
     */
    private BigDecimal calculateCurrentBalanceForCustomer(Long customerId) {
        return ledgerEntryRepository.calculateCurrentBalanceForCustomer(customerId);
    }

    /**
     * Recalculate and update customer balance
     */
    private void recalculateCustomerBalance(Long customerId) {
        BigDecimal newBalance = calculateCurrentBalanceForCustomer(customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));
        
        customer.setCurrentBalance(newBalance);
        customerRepository.save(customer);
    }

    /**
     * Convert LedgerEntry entity to LedgerEntryResponse DTO
     */
    private LedgerEntryResponse convertToResponse(LedgerEntry entry) {
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
        response.setCreatedByUsername(entry.getCreatedBy().getUsername());
        response.setUpdatedByUsername(entry.getUpdatedBy() != null ? entry.getUpdatedBy().getUsername() : null);
        response.setCreatedAt(entry.getCreatedAt());
        response.setUpdatedAt(entry.getUpdatedAt());

        // Convert attachments
        if (entry.getAttachments() != null && !entry.getAttachments().isEmpty()) {
            response.setAttachments(entry.getAttachments().stream()
                    .filter(DocumentAttachment::isActive)
                    .map(this::convertToAttachmentResponse)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    /**
     * Convert DocumentAttachment to DocumentAttachmentResponse
     */
    private DocumentAttachmentResponse convertToAttachmentResponse(DocumentAttachment attachment) {
        DocumentAttachmentResponse response = new DocumentAttachmentResponse();
        response.setId(attachment.getId());
        response.setFileName(attachment.getFileName());
        response.setOriginalFileName(attachment.getOriginalFileName());
        response.setContentType(attachment.getContentType());
        response.setFileSize(attachment.getFileSize());
        response.setDescription(attachment.getDescription());
        response.setActive(attachment.isActive());
        response.setUploadedAt(attachment.getUploadedAt());
        response.setUploadedByUsername(attachment.getUploadedBy().getUsername());
        response.setDownloadUrl("/api/v1/files/download/" + attachment.getId());
        return response;
    }

    /**
     * Get current authenticated user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    /**
     * Inner class for customer balance summary
     */
    public static class CustomerBalanceSummary {
        private final Long customerId;
        private final String customerName;
        private final BigDecimal totalCredit;
        private final BigDecimal totalDebit;
        private final BigDecimal currentBalance;
        private final long totalTransactions;
        private final BigDecimal creditLimit;

        public CustomerBalanceSummary(Long customerId, String customerName, BigDecimal totalCredit, 
                                    BigDecimal totalDebit, BigDecimal currentBalance, long totalTransactions,
                                    BigDecimal creditLimit) {
            this.customerId = customerId;
            this.customerName = customerName;
            this.totalCredit = totalCredit;
            this.totalDebit = totalDebit;
            this.currentBalance = currentBalance;
            this.totalTransactions = totalTransactions;
            this.creditLimit = creditLimit;
        }

        // Getters
        public Long getCustomerId() { return customerId; }
        public String getCustomerName() { return customerName; }
        public BigDecimal getTotalCredit() { return totalCredit; }
        public BigDecimal getTotalDebit() { return totalDebit; }
        public BigDecimal getCurrentBalance() { return currentBalance; }
        public long getTotalTransactions() { return totalTransactions; }
        public BigDecimal getCreditLimit() { return creditLimit; }
        
        public boolean isOverCreditLimit() {
            return creditLimit != null && creditLimit.compareTo(BigDecimal.ZERO) > 0 && 
                   currentBalance.compareTo(creditLimit) > 0;
        }
    }
}
