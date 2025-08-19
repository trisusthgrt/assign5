package com.example.ledgerly.service;

import com.example.ledgerly.dto.PaymentDisputeRequest;
import com.example.ledgerly.dto.PaymentResponse;
import com.example.ledgerly.dto.PaymentStatusUpdateRequest;
import com.example.ledgerly.entity.Payment;
import com.example.ledgerly.entity.PaymentStatus;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.exception.BusinessRuleException;
import com.example.ledgerly.repository.PaymentRepository;
import com.example.ledgerly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing payment status tracking and updates
 */
@Service
@Transactional
public class PaymentStatusService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    @Autowired
    public PaymentStatusService(PaymentRepository paymentRepository,
                               UserRepository userRepository,
                               AuditService auditService) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    /**
     * Update payment status
     */
    public PaymentResponse updatePaymentStatus(Long paymentId, PaymentStatusUpdateRequest request) {
        User currentUser = getCurrentUser();

        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

            // Create audit snapshot of old values
            Map<String, Object> oldValues = auditService.createAuditSnapshot(payment);

            PaymentStatus oldStatus = payment.getStatus();

            // Validate status transition
            validateStatusTransition(payment, request.getStatus(), currentUser);

            // Update status based on new status
            switch (request.getStatus()) {
                case PAID:
                    payment.markAsPaid(currentUser, request.getStatusNotes());
                    break;
                case OVERDUE:
                    int overdueDays = payment.calculateOverdueDays();
                    payment.markAsOverdue(currentUser, overdueDays);
                    break;
                case PENDING:
                case PARTIAL:
                case DISPUTED:
                case PROCESSED:
                case CANCELLED:
                case FAILED:
                case REFUNDED:
                case IN_COLLECTION:
                case WRITTEN_OFF:
                default:
                    payment.updateStatus(request.getStatus(), currentUser, request.getStatusNotes());
                    break;
            }

            // Update due date if provided
            if (request.getDueDate() != null) {
                payment.setDueDate(request.getDueDate());
            }

            Payment updatedPayment = paymentRepository.save(payment);

            // Create audit snapshot of new values
            Map<String, Object> newValues = auditService.createAuditSnapshot(updatedPayment);

            // Log successful status update
            auditService.logSuccess("UPDATE_PAYMENT_STATUS", "PAYMENT", updatedPayment.getId(),
                    oldValues, newValues,
                    String.format("Updated payment status from %s to %s for payment %d", 
                                oldStatus, request.getStatus(), paymentId),
                    currentUser);

            return convertToPaymentResponse(updatedPayment);

        } catch (BusinessRuleException e) {
            throw e;
        } catch (Exception e) {
            auditService.logFailure("UPDATE_PAYMENT_STATUS", "PAYMENT", paymentId,
                    e.getMessage(),
                    String.format("Failed to update payment status for payment %d", paymentId),
                    currentUser);
            throw e;
        }
    }

    /**
     * Mark payment as disputed
     */
    public PaymentResponse disputePayment(Long paymentId, PaymentDisputeRequest request) {
        User currentUser = getCurrentUser();

        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

            // Create audit snapshot of old values
            Map<String, Object> oldValues = auditService.createAuditSnapshot(payment);

            // Mark as disputed
            String fullReason = request.getDisputeReason();
            if (request.getAdditionalNotes() != null && !request.getAdditionalNotes().trim().isEmpty()) {
                fullReason += " | Additional notes: " + request.getAdditionalNotes();
            }

            payment.markAsDisputed(currentUser, fullReason);
            Payment updatedPayment = paymentRepository.save(payment);

            // Create audit snapshot of new values
            Map<String, Object> newValues = auditService.createAuditSnapshot(updatedPayment);

            // Log successful dispute
            auditService.logSuccess("DISPUTE_PAYMENT", "PAYMENT", updatedPayment.getId(),
                    oldValues, newValues,
                    String.format("Payment %d disputed by %s: %s", 
                                paymentId, currentUser.getUsername(), request.getDisputeReason()),
                    currentUser);

            return convertToPaymentResponse(updatedPayment);

        } catch (BusinessRuleException e) {
            throw e;
        } catch (Exception e) {
            auditService.logFailure("DISPUTE_PAYMENT", "PAYMENT", paymentId,
                    e.getMessage(),
                    String.format("Failed to dispute payment %d", paymentId),
                    currentUser);
            throw e;
        }
    }

    /**
     * Resolve payment dispute
     */
    public PaymentResponse resolveDispute(Long paymentId, String resolutionNotes) {
        User currentUser = getCurrentUser();

        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

            // Create audit snapshot of old values
            Map<String, Object> oldValues = auditService.createAuditSnapshot(payment);

            // Resolve dispute
            payment.resolveDispute(currentUser, resolutionNotes);
            Payment updatedPayment = paymentRepository.save(payment);

            // Create audit snapshot of new values
            Map<String, Object> newValues = auditService.createAuditSnapshot(updatedPayment);

            // Log successful resolution
            auditService.logSuccess("RESOLVE_DISPUTE", "PAYMENT", updatedPayment.getId(),
                    oldValues, newValues,
                    String.format("Payment %d dispute resolved by %s: %s", 
                                paymentId, currentUser.getUsername(), resolutionNotes),
                    currentUser);

            return convertToPaymentResponse(updatedPayment);

        } catch (BusinessRuleException e) {
            throw e;
        } catch (Exception e) {
            auditService.logFailure("RESOLVE_DISPUTE", "PAYMENT", paymentId,
                    e.getMessage(),
                    String.format("Failed to resolve dispute for payment %d", paymentId),
                    currentUser);
            throw e;
        }
    }

    /**
     * Get payments by status with pagination
     */
    public Page<PaymentResponse> getPaymentsByStatus(PaymentStatus status, int page, int size, 
                                                    String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Payment> payments = paymentRepository.findByStatusAndIsActiveTrueOrderByPaymentDateDesc(status, pageable);
        
        return payments.map(this::convertToPaymentResponse);
    }

    /**
     * Get overdue payments
     */
    public List<PaymentResponse> getOverduePayments() {
        List<Payment> overduePayments = paymentRepository.findByStatusAndIsActiveTrueOrderByPaymentDateDesc(PaymentStatus.OVERDUE);
        return overduePayments.stream()
                .map(this::convertToPaymentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get disputed payments
     */
    public List<PaymentResponse> getDisputedPayments() {
        List<Payment> disputedPayments = paymentRepository.findByStatusAndIsActiveTrueOrderByPaymentDateDesc(PaymentStatus.DISPUTED);
        return disputedPayments.stream()
                .map(this::convertToPaymentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get payment status summary
     */
    public Map<String, Object> getPaymentStatusSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        // Count by status
        Map<String, Long> statusCounts = new HashMap<>();
        for (PaymentStatus status : PaymentStatus.values()) {
            long count = paymentRepository.countByStatusAndIsActiveTrue(status);
            statusCounts.put(status.name(), count);
        }
        
        // Get total counts
        long totalPayments = paymentRepository.countByIsActiveTrue();
        long problematicPayments = statusCounts.getOrDefault("OVERDUE", 0L) + 
                                 statusCounts.getOrDefault("DISPUTED", 0L) + 
                                 statusCounts.getOrDefault("FAILED", 0L);
        
        summary.put("totalPayments", totalPayments);
        summary.put("statusCounts", statusCounts);
        summary.put("problematicPayments", problematicPayments);
        summary.put("problematicPercentage", totalPayments > 0 ? 
                   (double) problematicPayments / totalPayments * 100 : 0.0);
        
        return summary;
    }

    /**
     * Automatically check for overdue payments (scheduled task)
     */
    @Scheduled(cron = "0 0 9 * * ?") // Run daily at 9 AM
    public void checkOverduePayments() {
        try {
            List<Payment> pendingPayments = paymentRepository.findByStatusAndIsActiveTrueOrderByPaymentDateDesc(PaymentStatus.PENDING);
            pendingPayments.addAll(paymentRepository.findByStatusAndIsActiveTrueOrderByPaymentDateDesc(PaymentStatus.PARTIAL));

            int overdueCount = 0;
            User systemUser = getSystemUser();

            for (Payment payment : pendingPayments) {
                if (payment.isOverdue() && payment.getStatus() != PaymentStatus.OVERDUE) {
                    int daysOverdue = payment.calculateOverdueDays();
                    payment.markAsOverdue(systemUser, daysOverdue);
                    paymentRepository.save(payment);
                    overdueCount++;

                    // Log automatic status update
                    auditService.logSuccess("AUTO_MARK_OVERDUE", "PAYMENT", payment.getId(),
                            null, auditService.createAuditSnapshot(payment),
                            String.format("Automatically marked payment %d as overdue (%d days)", 
                                        payment.getId(), daysOverdue),
                            systemUser);
                }
            }

            if (overdueCount > 0) {
                auditService.logSuccess("OVERDUE_CHECK_COMPLETED", "SYSTEM", null,
                        null, null,
                        String.format("Checked for overdue payments, marked %d payments as overdue", overdueCount),
                        systemUser);
            }

        } catch (Exception e) {
            User systemUser = getSystemUser();
            auditService.logFailure("OVERDUE_CHECK_FAILED", "SYSTEM", null,
                    e.getMessage(),
                    "Failed to check for overdue payments during scheduled task",
                    systemUser);
        }
    }

    /**
     * Validate status transition
     */
    private void validateStatusTransition(Payment payment, PaymentStatus newStatus, User user) {
        PaymentStatus currentStatus = payment.getStatus();

        // Check if current status allows updates
        if (!currentStatus.canBeUpdated() && newStatus != currentStatus) {
            throw new BusinessRuleException("INVALID_STATUS_TRANSITION",
                    String.format("Cannot update payment status from %s to %s", currentStatus, newStatus));
        }

        // Specific transition validations
        switch (newStatus) {
            case PAID:
                if (!currentStatus.canBePaid()) {
                    throw new BusinessRuleException("CANNOT_MARK_PAID",
                            String.format("Cannot mark payment as paid from status %s", currentStatus));
                }
                break;
            case DISPUTED:
                if (!currentStatus.canBeDisputed()) {
                    throw new BusinessRuleException("CANNOT_DISPUTE",
                            String.format("Cannot dispute payment from status %s", currentStatus));
                }
                break;
        }
    }

    /**
     * Convert Payment entity to PaymentResponse DTO
     */
    private PaymentResponse convertToPaymentResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setPaymentDate(payment.getPaymentDate());
        response.setAmount(payment.getAmount());
        response.setAppliedAmount(payment.getAppliedAmount());
        response.setRemainingAmount(payment.getRemainingAmount());
        response.setDescription(payment.getDescription());
        response.setNotes(payment.getNotes());
        response.setReferenceNumber(payment.getReferenceNumber());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setStatus(payment.getStatus());
        response.setBankDetails(payment.getBankDetails());
        response.setCheckNumber(payment.getCheckNumber());
        response.setProcessedDate(payment.getProcessedDate());
        response.setAdvancePayment(payment.isAdvancePayment());
        response.setActive(payment.isActive());
        response.setCustomerId(payment.getCustomer().getId());
        response.setCustomerName(payment.getCustomer().getName());
        response.setCreatedByUsername(payment.getCreatedBy().getUsername());
        response.setUpdatedByUsername(payment.getUpdatedBy() != null ? payment.getUpdatedBy().getUsername() : null);
        response.setCreatedAt(payment.getCreatedAt());
        response.setUpdatedAt(payment.getUpdatedAt());

        // Status tracking fields
        response.setDueDate(payment.getDueDate());
        response.setStatusUpdatedAt(payment.getStatusUpdatedAt());
        response.setStatusUpdatedByUsername(payment.getStatusUpdatedBy() != null ? payment.getStatusUpdatedBy().getUsername() : null);
        response.setStatusNotes(payment.getStatusNotes());
        response.setDisputeDate(payment.getDisputeDate());
        response.setDisputeReason(payment.getDisputeReason());
        response.setDisputedByUsername(payment.getDisputedBy() != null ? payment.getDisputedBy().getUsername() : null);
        response.setOverdueDays(payment.getOverdueDays());
        response.setLastReminderSent(payment.getLastReminderSent());
        response.setReminderCount(payment.getReminderCount());

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
     * Get system user for automated tasks
     */
    private User getSystemUser() {
        return userRepository.findByUsernameOrEmail("system")
                .orElse(getCurrentUser()); // Fallback to current user if system user doesn't exist
    }
}
