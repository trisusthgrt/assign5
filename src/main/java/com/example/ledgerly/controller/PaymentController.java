package com.example.ledgerly.controller;

import com.example.ledgerly.dto.*;
import com.example.ledgerly.entity.PaymentStatus;
import com.example.ledgerly.exception.BusinessRuleException;
import com.example.ledgerly.service.PaymentService;
import com.example.ledgerly.service.PaymentStatusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for Payment management operations
 */
@RestController
@RequestMapping("/api/v1/payments")
@PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentStatusService paymentStatusService;

    @Autowired
    public PaymentController(PaymentService paymentService, PaymentStatusService paymentStatusService) {
        this.paymentService = paymentService;
        this.paymentStatusService = paymentStatusService;
    }

    /**
     * Record a new payment
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> recordPayment(@Valid @RequestBody PaymentCreateRequest request) {
        try {
            PaymentResponse payment = paymentService.recordPayment(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment recorded successfully");
            response.put("payment", payment);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (BusinessRuleException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("ruleCode", e.getRuleCode());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to record payment: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Apply payment to ledger entries (partial settlement)
     */
    @PostMapping("/apply")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> applyPaymentToEntries(@Valid @RequestBody PaymentApplicationRequest request) {
        try {
            PaymentResponse payment = paymentService.applyPaymentToEntries(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment applied successfully");
            response.put("payment", payment);
            response.put("applicationsCount", request.getApplications().size());
            
            return ResponseEntity.ok(response);
        } catch (BusinessRuleException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("ruleCode", e.getRuleCode());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to apply payment: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Auto-apply payment to oldest outstanding entries
     */
    @PostMapping("/{paymentId}/auto-apply")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> autoApplyPayment(@PathVariable Long paymentId) {
        try {
            PaymentResponse payment = paymentService.autoApplyPayment(paymentId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment auto-applied successfully");
            response.put("payment", payment);
            
            return ResponseEntity.ok(response);
        } catch (BusinessRuleException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("ruleCode", e.getRuleCode());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to auto-apply payment: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Reverse payment application
     */
    @DeleteMapping("/applications/{applicationId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> reversePaymentApplication(@PathVariable Long applicationId) {
        try {
            paymentService.reversePaymentApplication(applicationId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment application reversed successfully");
            
            return ResponseEntity.ok(response);
        } catch (BusinessRuleException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("ruleCode", e.getRuleCode());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to reverse payment application: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Get payment by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPaymentById(@PathVariable Long id) {
        try {
            PaymentResponse payment = paymentService.getPaymentById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("payment", payment);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }

    /**
     * Get payments for a customer with pagination
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getPaymentsForCustomer(
            @PathVariable Long customerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Page<PaymentResponse> payments = paymentService.getPaymentsForCustomer(
                    customerId, page, size, sortBy, sortDir);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("payments", payments.getContent());
            response.put("totalElements", payments.getTotalElements());
            response.put("totalPages", payments.getTotalPages());
            response.put("currentPage", payments.getNumber());
            response.put("size", payments.getSize());
            response.put("customerId", customerId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch payments: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get unapplied payments for a customer
     */
    @GetMapping("/customer/{customerId}/unapplied")
    public ResponseEntity<Map<String, Object>> getUnappliedPayments(@PathVariable Long customerId) {
        try {
            List<PaymentResponse> payments = paymentService.getUnappliedPayments(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("unappliedPayments", payments);
            response.put("count", payments.size());
            response.put("customerId", customerId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch unapplied payments: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get outstanding balance for a customer
     */
    @GetMapping("/customer/{customerId}/outstanding-balance")
    public ResponseEntity<Map<String, Object>> getOutstandingBalance(@PathVariable Long customerId) {
        try {
            OutstandingBalanceResponse outstandingBalance = paymentService.getOutstandingBalance(customerId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("outstandingBalance", outstandingBalance);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch outstanding balance: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get all payment statuses
     */
    @GetMapping("/statuses")
    public ResponseEntity<Map<String, Object>> getPaymentStatuses() {
        try {
            PaymentStatus[] statuses = PaymentStatus.values();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("paymentStatuses", statuses);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch payment statuses: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get payment summary for a customer
     */
    @GetMapping("/customer/{customerId}/summary")
    public ResponseEntity<Map<String, Object>> getPaymentSummary(@PathVariable Long customerId) {
        try {
            OutstandingBalanceResponse outstandingBalance = paymentService.getOutstandingBalance(customerId);
            List<PaymentResponse> unappliedPayments = paymentService.getUnappliedPayments(customerId);
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("customerId", customerId);
            summary.put("customerName", outstandingBalance.getCustomerName());
            summary.put("totalOutstandingBalance", outstandingBalance.getTotalOutstandingBalance());
            summary.put("totalUnappliedPayments", outstandingBalance.getTotalUnappliedPayments());
            summary.put("netOutstandingBalance", outstandingBalance.getNetOutstandingBalance());
            summary.put("outstandingEntriesCount", outstandingBalance.getTotalOutstandingEntries());
            summary.put("unappliedPaymentsCount", unappliedPayments.size());
            summary.put("oldestOutstandingDate", outstandingBalance.getOldestOutstandingDate());
            summary.put("averageDaysOutstanding", outstandingBalance.getAverageDaysOutstanding());
            summary.put("hasOutstandingBalance", outstandingBalance.hasOutstandingBalance());
            summary.put("hasUnappliedPayments", outstandingBalance.hasUnappliedPayments());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("summary", summary);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch payment summary: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get settlement suggestions for a customer
     */
    @GetMapping("/customer/{customerId}/settlement-suggestions")
    public ResponseEntity<Map<String, Object>> getSettlementSuggestions(@PathVariable Long customerId) {
        try {
            OutstandingBalanceResponse outstandingBalance = paymentService.getOutstandingBalance(customerId);
            List<PaymentResponse> unappliedPayments = paymentService.getUnappliedPayments(customerId);
            
            List<Map<String, Object>> suggestions = new ArrayList<>();
            
            // Create settlement suggestions by matching payments to outstanding entries
            for (PaymentResponse payment : unappliedPayments) {
                if (payment.getUnappliedAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                    List<Map<String, Object>> applicableEntries = new ArrayList<>();
                    java.math.BigDecimal remainingAmount = payment.getUnappliedAmount();
                    
                    for (OutstandingBalanceResponse.OutstandingEntry entry : outstandingBalance.getOutstandingEntries()) {
                        if (remainingAmount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                            break;
                        }
                        
                        java.math.BigDecimal applicationAmount = remainingAmount.min(entry.getOutstandingAmount());
                        
                        Map<String, Object> application = new HashMap<>();
                        application.put("ledgerEntryId", entry.getLedgerEntryId());
                        application.put("description", entry.getDescription());
                        application.put("outstandingAmount", entry.getOutstandingAmount());
                        application.put("suggestedAmount", applicationAmount);
                        application.put("daysOutstanding", entry.getDaysOutstanding());
                        
                        applicableEntries.add(application);
                        remainingAmount = remainingAmount.subtract(applicationAmount);
                    }
                    
                    if (!applicableEntries.isEmpty()) {
                        Map<String, Object> suggestion = new HashMap<>();
                        suggestion.put("paymentId", payment.getId());
                        suggestion.put("paymentDescription", payment.getDescription());
                        suggestion.put("paymentAmount", payment.getAmount());
                        suggestion.put("unappliedAmount", payment.getUnappliedAmount());
                        suggestion.put("suggestedApplications", applicableEntries);
                        suggestion.put("totalSuggestedAmount", payment.getUnappliedAmount().subtract(remainingAmount));
                        
                        suggestions.add(suggestion);
                    }
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customerId", customerId);
            response.put("suggestions", suggestions);
            response.put("suggestionsCount", suggestions.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to generate settlement suggestions: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ========================================
    // Payment Status Management Endpoints
    // ========================================

    /**
     * Update payment status
     */
    @PutMapping("/{paymentId}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> updatePaymentStatus(
            @PathVariable Long paymentId,
            @Valid @RequestBody PaymentStatusUpdateRequest request) {
        try {
            PaymentResponse payment = paymentStatusService.updatePaymentStatus(paymentId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment status updated successfully");
            response.put("payment", payment);
            response.put("newStatus", request.getStatus());
            
            return ResponseEntity.ok(response);
        } catch (BusinessRuleException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("ruleCode", e.getRuleCode());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update payment status: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Mark payment as disputed
     */
    @PostMapping("/{paymentId}/dispute")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> disputePayment(
            @PathVariable Long paymentId,
            @Valid @RequestBody PaymentDisputeRequest request) {
        try {
            PaymentResponse payment = paymentStatusService.disputePayment(paymentId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment disputed successfully");
            response.put("payment", payment);
            response.put("disputeReason", request.getDisputeReason());
            
            return ResponseEntity.ok(response);
        } catch (BusinessRuleException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("ruleCode", e.getRuleCode());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to dispute payment: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Resolve payment dispute
     */
    @PostMapping("/{paymentId}/resolve-dispute")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> resolveDispute(
            @PathVariable Long paymentId,
            @RequestParam String resolutionNotes) {
        try {
            PaymentResponse payment = paymentStatusService.resolveDispute(paymentId, resolutionNotes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Payment dispute resolved successfully");
            response.put("payment", payment);
            response.put("resolutionNotes", resolutionNotes);
            
            return ResponseEntity.ok(response);
        } catch (BusinessRuleException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("ruleCode", e.getRuleCode());
            errorResponse.put("message", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to resolve dispute: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Get payments by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getPaymentsByStatus(
            @PathVariable PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "paymentDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Page<PaymentResponse> payments = paymentStatusService.getPaymentsByStatus(
                    status, page, size, sortBy, sortDir);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("status", status);
            response.put("payments", payments.getContent());
            response.put("totalElements", payments.getTotalElements());
            response.put("totalPages", payments.getTotalPages());
            response.put("currentPage", payments.getNumber());
            response.put("size", payments.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch payments by status: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get overdue payments
     */
    @GetMapping("/overdue")
    public ResponseEntity<Map<String, Object>> getOverduePayments() {
        try {
            List<PaymentResponse> overduePayments = paymentStatusService.getOverduePayments();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("overduePayments", overduePayments);
            response.put("count", overduePayments.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch overdue payments: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get disputed payments
     */
    @GetMapping("/disputed")
    public ResponseEntity<Map<String, Object>> getDisputedPayments() {
        try {
            List<PaymentResponse> disputedPayments = paymentStatusService.getDisputedPayments();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("disputedPayments", disputedPayments);
            response.put("count", disputedPayments.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch disputed payments: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get payment status summary
     */
    @GetMapping("/status-summary")
    public ResponseEntity<Map<String, Object>> getPaymentStatusSummary() {
        try {
            Map<String, Object> summary = paymentStatusService.getPaymentStatusSummary();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("summary", summary);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch payment status summary: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
