package com.example.ledgerly.controller;

import com.example.ledgerly.dto.LedgerEntryCreateRequest;
import com.example.ledgerly.dto.LedgerEntryResponse;
import com.example.ledgerly.dto.LedgerEntryUpdateRequest;
import com.example.ledgerly.entity.TransactionType;
import com.example.ledgerly.exception.BusinessRuleException;
import com.example.ledgerly.exception.InsufficientBalanceException;
import com.example.ledgerly.exception.InvalidTransactionAmountException;
import com.example.ledgerly.service.LedgerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Ledger management operations
 */
@RestController
@RequestMapping("/api/v1/ledger")
@PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
public class LedgerController {

    private final LedgerService ledgerService;

    @Autowired
    public LedgerController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    /**
     * Create a new ledger entry
     */
    @PostMapping("/entries")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> createLedgerEntry(@Valid @RequestBody LedgerEntryCreateRequest request) {
        try {
            LedgerEntryResponse entry = ledgerService.createLedgerEntry(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ledger entry created successfully");
            response.put("entry", entry);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (InsufficientBalanceException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("ruleCode", e.getRuleCode());
            errorResponse.put("message", e.getMessage());
            errorResponse.put("details", Map.of(
                    "currentBalance", e.getCurrentBalance(),
                    "requestedAmount", e.getRequestedAmount(),
                    "resultingBalance", e.getResultingBalance()
            ));
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (InvalidTransactionAmountException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("ruleCode", e.getRuleCode());
            errorResponse.put("message", e.getMessage());
            errorResponse.put("details", Map.of(
                    "amount", e.getAmount(),
                    "validationRule", e.getValidationRule()
            ));
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (BusinessRuleException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("ruleCode", e.getRuleCode());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to create ledger entry: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Create a new ledger entry with file attachments
     */
    @PostMapping("/entries/with-attachments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> createLedgerEntryWithAttachments(
            @Valid @RequestPart("entry") LedgerEntryCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "attachmentDescription", required = false) String attachmentDescription) {
        try {
            LedgerEntryResponse entry = ledgerService.createLedgerEntryWithAttachments(
                    request, files, attachmentDescription);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ledger entry created successfully with attachments");
            response.put("entry", entry);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to create ledger entry with attachments: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Get ledger entry by ID
     */
    @GetMapping("/entries/{id}")
    public ResponseEntity<Map<String, Object>> getLedgerEntryById(@PathVariable Long id) {
        try {
            LedgerEntryResponse entry = ledgerService.getLedgerEntryById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("entry", entry);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Update an existing ledger entry
     */
    @PutMapping("/entries/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> updateLedgerEntry(@PathVariable Long id, 
                                                               @Valid @RequestBody LedgerEntryUpdateRequest request) {
        try {
            LedgerEntryResponse entry = ledgerService.updateLedgerEntry(id, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ledger entry updated successfully");
            response.put("entry", entry);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update ledger entry: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Delete a ledger entry (soft delete)
     */
    @DeleteMapping("/entries/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> deleteLedgerEntry(@PathVariable Long id) {
        try {
            ledgerService.deleteLedgerEntry(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Ledger entry deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Get ledger entries for a customer with pagination
     */
    @GetMapping("/customer/{customerId}/entries")
    public ResponseEntity<Map<String, Object>> getLedgerEntriesForCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Page<LedgerEntryResponse> entries = ledgerService.getLedgerEntriesForCustomer(
                    customerId, page, size, sortBy, sortDir);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("entries", entries.getContent());
            response.put("totalElements", entries.getTotalElements());
            response.put("totalPages", entries.getTotalPages());
            response.put("currentPage", entries.getNumber());
            response.put("size", entries.getSize());
            response.put("customerId", customerId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch ledger entries: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Search ledger entries by multiple criteria
     */
    @GetMapping("/entries/search")
    public ResponseEntity<Map<String, Object>> searchLedgerEntries(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) TransactionType transactionType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) BigDecimal minAmount,
            @RequestParam(required = false) BigDecimal maxAmount,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String referenceNumber,
            @RequestParam(required = false) String invoiceNumber,
            @RequestParam(required = false) Boolean isReconciled,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Page<LedgerEntryResponse> entries = ledgerService.searchLedgerEntries(
                    customerId, transactionType, startDate, endDate, minAmount, maxAmount,
                    description, referenceNumber, invoiceNumber, isReconciled,
                    page, size, sortBy, sortDir);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("entries", entries.getContent());
            response.put("totalElements", entries.getTotalElements());
            response.put("totalPages", entries.getTotalPages());
            response.put("currentPage", entries.getNumber());
            response.put("size", entries.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to search ledger entries: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get customer balance summary
     */
    @GetMapping("/customer/{customerId}/balance-summary")
    public ResponseEntity<Map<String, Object>> getCustomerBalanceSummary(@PathVariable Long customerId) {
        try {
            LedgerService.CustomerBalanceSummary summary = ledgerService.getCustomerBalanceSummary(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("summary", Map.of(
                    "customerId", summary.getCustomerId(),
                    "customerName", summary.getCustomerName(),
                    "totalCredit", summary.getTotalCredit(),
                    "totalDebit", summary.getTotalDebit(),
                    "currentBalance", summary.getCurrentBalance(),
                    "totalTransactions", summary.getTotalTransactions(),
                    "creditLimit", summary.getCreditLimit(),
                    "isOverCreditLimit", summary.isOverCreditLimit()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch balance summary: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get unreconciled entries for a customer
     */
    @GetMapping("/customer/{customerId}/unreconciled")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> getUnreconciledEntries(@PathVariable Long customerId) {
        try {
            List<LedgerEntryResponse> entries = ledgerService.getUnreconciledEntries(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("entries", entries);
            response.put("count", entries.size());
            response.put("customerId", customerId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch unreconciled entries: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Add attachment to existing ledger entry
     */
    @PostMapping("/entries/{entryId}/attachments")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> addAttachmentToEntry(
            @PathVariable Long entryId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        try {
            LedgerEntryResponse entry = ledgerService.addAttachmentToEntry(entryId, file, description);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Attachment added successfully");
            response.put("entry", entry);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to add attachment: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Remove attachment from ledger entry
     */
    @DeleteMapping("/entries/{entryId}/attachments/{attachmentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> removeAttachmentFromEntry(
            @PathVariable Long entryId,
            @PathVariable Long attachmentId) {
        try {
            ledgerService.removeAttachmentFromEntry(entryId, attachmentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Attachment removed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to remove attachment: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Get all transaction types
     */
    @GetMapping("/transaction-types")
    public ResponseEntity<Map<String, Object>> getTransactionTypes() {
        try {
            TransactionType[] types = TransactionType.values();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("transactionTypes", types);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch transaction types: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get ledger entries by date range
     */
    @GetMapping("/entries/date-range")
    public ResponseEntity<Map<String, Object>> getLedgerEntriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "transactionDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Page<LedgerEntryResponse> entries = ledgerService.searchLedgerEntries(
                    customerId, null, startDate, endDate, null, null,
                    null, null, null, null,
                    page, size, sortBy, sortDir);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("entries", entries.getContent());
            response.put("totalElements", entries.getTotalElements());
            response.put("totalPages", entries.getTotalPages());
            response.put("currentPage", entries.getNumber());
            response.put("size", entries.getSize());
            response.put("startDate", startDate);
            response.put("endDate", endDate);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch entries by date range: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
