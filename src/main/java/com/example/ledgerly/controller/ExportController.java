package com.example.ledgerly.controller;

import com.example.ledgerly.service.ExportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/export")
@Tag(name = "Export Management", description = "APIs for exporting customer statements and transaction history")
public class ExportController {

    private static final Logger logger = LoggerFactory.getLogger(ExportController.class);

    private final ExportService exportService;

    @Autowired
    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    /**
     * Export customer statement as PDF for a specific date range
     */
    @GetMapping("/customers/{customerId}/statement/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @Operation(
        summary = "Export Customer Statement as PDF",
        description = "Generate and download a PDF statement for a customer within a specified date range. " +
                    "Staff can only export statements for customers in their assigned shop."
    )
    public ResponseEntity<byte[]> exportCustomerStatementPDF(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long customerId,
            
            @Parameter(description = "Start date (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            Authentication authentication) {
        
        try {
            logger.info("Exporting PDF statement for customer {} from {} to {}", customerId, startDate, endDate);
            
            String username = authentication.getName();
            byte[] pdfContent = exportService.generateCustomerStatementPDF(customerId, startDate, endDate, username);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                String.format("customer_statement_%d_%s_to_%s.pdf", customerId, startDate, endDate));
            headers.setContentLength(pdfContent.length);
            
            logger.info("Successfully generated PDF statement for customer {}", customerId);
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error exporting PDF statement for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Export customer statement as CSV for a specific date range
     */
    @GetMapping("/customers/{customerId}/statement/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @Operation(
        summary = "Export Customer Statement as CSV",
        description = "Generate and download a CSV statement for a customer within a specified date range. " +
                    "Staff can only export statements for customers in their assigned shop."
    )
    public ResponseEntity<byte[]> exportCustomerStatementCSV(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long customerId,
            
            @Parameter(description = "Start date (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date (YYYY-MM-DD)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            Authentication authentication) {
        
        try {
            logger.info("Exporting CSV statement for customer {} from {} to {}", customerId, startDate, endDate);
            
            String username = authentication.getName();
            byte[] csvContent = exportService.generateCustomerStatementCSV(customerId, startDate, endDate, username);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", 
                String.format("customer_statement_%d_%s_to_%s.csv", customerId, startDate, endDate));
            headers.setContentLength(csvContent.length);
            
            logger.info("Successfully generated CSV statement for customer {}", customerId);
            return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error exporting CSV statement for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Export complete transaction history as PDF for a customer
     */
    @GetMapping("/customers/{customerId}/history/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @Operation(
        summary = "Export Complete Transaction History as PDF",
        description = "Generate and download a PDF containing the complete transaction history for a customer. " +
                    "Staff can only export history for customers in their assigned shop."
    )
    public ResponseEntity<byte[]> exportTransactionHistoryPDF(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long customerId,
            
            Authentication authentication) {
        
        try {
            logger.info("Exporting PDF transaction history for customer {}", customerId);
            
            String username = authentication.getName();
            byte[] pdfContent = exportService.generateTransactionHistoryPDF(customerId, username);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                String.format("customer_transaction_history_%d.pdf", customerId));
            headers.setContentLength(pdfContent.length);
            
            logger.info("Successfully generated PDF transaction history for customer {}", customerId);
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error exporting PDF transaction history for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Export complete transaction history as CSV for a customer
     */
    @GetMapping("/customers/{customerId}/history/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @Operation(
        summary = "Export Complete Transaction History as CSV",
        description = "Generate and download a CSV containing the complete transaction history for a customer. " +
                    "Staff can only export history for customers in their assigned shop."
    )
    public ResponseEntity<byte[]> exportTransactionHistoryCSV(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long customerId,
            
            Authentication authentication) {
        
        try {
            logger.info("Exporting CSV transaction history for customer {}", customerId);
            
            String username = authentication.getName();
            byte[] csvContent = exportService.generateTransactionHistoryCSV(customerId, username);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", 
                String.format("customer_transaction_history_%d.csv", customerId));
            headers.setContentLength(csvContent.length);
            
            logger.info("Successfully generated CSV transaction history for customer {}", customerId);
            return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error exporting CSV transaction history for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Export customer statement as PDF for current month
     */
    @GetMapping("/customers/{customerId}/statement/current-month/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @Operation(
        summary = "Export Current Month Statement as PDF",
        description = "Generate and download a PDF statement for a customer for the current month. " +
                    "Staff can only export statements for customers in their assigned shop."
    )
    public ResponseEntity<byte[]> exportCurrentMonthStatementPDF(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long customerId,
            
            Authentication authentication) {
        
        try {
            LocalDate now = LocalDate.now();
            LocalDate startDate = now.withDayOfMonth(1);
            LocalDate endDate = now;
            
            logger.info("Exporting current month PDF statement for customer {}", customerId);
            
            String username = authentication.getName();
            byte[] pdfContent = exportService.generateCustomerStatementPDF(customerId, startDate, endDate, username);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", 
                String.format("customer_statement_%d_current_month.pdf", customerId));
            headers.setContentLength(pdfContent.length);
            
            logger.info("Successfully generated current month PDF statement for customer {}", customerId);
            return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error exporting current month PDF statement for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Export customer statement as CSV for current month
     */
    @GetMapping("/customers/{customerId}/statement/current-month/csv")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    @Operation(
        summary = "Export Current Month Statement as CSV",
        description = "Generate and download a CSV statement for a customer for the current month. " +
                    "Staff can only export statements for customers in their assigned shop."
    )
    public ResponseEntity<byte[]> exportCurrentMonthStatementCSV(
            @Parameter(description = "Customer ID", required = true)
            @PathVariable Long customerId,
            
            Authentication authentication) {
        
        try {
            LocalDate now = LocalDate.now();
            LocalDate startDate = now.withDayOfMonth(1);
            LocalDate endDate = now;
            
            logger.info("Exporting current month CSV statement for customer {}", customerId);
            
            String username = authentication.getName();
            byte[] csvContent = exportService.generateCustomerStatementCSV(customerId, startDate, endDate, username);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", 
                String.format("customer_statement_%d_current_month.csv", customerId));
            headers.setContentLength(csvContent.length);
            
            logger.info("Successfully generated current month CSV statement for customer {}", customerId);
            return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            logger.error("Error exporting current month CSV statement for customer {}: {}", customerId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
