package com.example.ledgerly.service;

import com.example.ledgerly.dto.*;
import com.example.ledgerly.entity.*;
import com.example.ledgerly.exception.BusinessRuleException;
import com.example.ledgerly.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class for Payment operations including settlements and outstanding balance tracking
 */
@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentApplicationRepository paymentApplicationRepository;
    private final CustomerRepository customerRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final UserRepository userRepository;
    private final BusinessRuleService businessRuleService;
    private final AuditService auditService;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository,
                         PaymentApplicationRepository paymentApplicationRepository,
                         CustomerRepository customerRepository,
                         LedgerEntryRepository ledgerEntryRepository,
                         UserRepository userRepository,
                         BusinessRuleService businessRuleService,
                         AuditService auditService) {
        this.paymentRepository = paymentRepository;
        this.paymentApplicationRepository = paymentApplicationRepository;
        this.customerRepository = customerRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.userRepository = userRepository;
        this.businessRuleService = businessRuleService;
        this.auditService = auditService;
    }

    /**
     * Record a new payment
     */
    public PaymentResponse recordPayment(PaymentCreateRequest request) {
        User currentUser = getCurrentUser();

        try {
            // Validate customer
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found with id: " + request.getCustomerId()));

            // Validate customer status
            businessRuleService.validateCustomerStatus(customer, currentUser);

            // Validate payment amount
            businessRuleService.validateTransactionAmount(request.getAmount(), currentUser);

            // Create new payment
            Payment payment = new Payment();
            payment.setCustomer(customer);
            payment.setPaymentDate(request.getPaymentDate());
            payment.setAmount(request.getAmount());
            payment.setDescription(request.getDescription().trim());
            payment.setNotes(request.getNotes());
            payment.setReferenceNumber(request.getReferenceNumber());
            payment.setPaymentMethod(request.getPaymentMethod());
            payment.setStatus(request.getStatus() != null ? request.getStatus() : PaymentStatus.PENDING);
            payment.setBankDetails(request.getBankDetails());
            payment.setCheckNumber(request.getCheckNumber());
            payment.setAdvancePayment(request.isAdvancePayment());
            payment.setCreatedBy(currentUser);

            // Initialize applied and remaining amounts
            payment.setAppliedAmount(BigDecimal.ZERO);
            payment.setRemainingAmount(request.getAmount());

            // Create audit snapshot before saving
            Map<String, Object> auditSnapshot = auditService.createAuditSnapshot(payment);

            // Save payment
            Payment savedPayment = paymentRepository.save(payment);

            // Log successful creation
            auditService.logSuccess("RECORD_PAYMENT", "PAYMENT", savedPayment.getId(),
                    null, auditSnapshot,
                    String.format("Recorded payment of %s for customer %s", 
                                request.getAmount(), customer.getName()),
                    currentUser);

            return convertToPaymentResponse(savedPayment);

        } catch (BusinessRuleException e) {
            throw e;
        } catch (Exception e) {
            auditService.logFailure("RECORD_PAYMENT", "PAYMENT", null,
                    e.getMessage(),
                    String.format("Failed to record payment for customer %d", request.getCustomerId()),
                    currentUser);
            throw e;
        }
    }

    /**
     * Apply payment to ledger entries (partial settlement)
     */
    public PaymentResponse applyPaymentToEntries(PaymentApplicationRequest request) {
        User currentUser = getCurrentUser();

        try {
            // Get payment
            Payment payment = paymentRepository.findById(request.getPaymentId())
                    .orElseThrow(() -> new RuntimeException("Payment not found with id: " + request.getPaymentId()));

            // Validate payment can be applied
            if (payment.getStatus() == PaymentStatus.CANCELLED || 
                payment.getStatus() == PaymentStatus.FAILED || 
                !payment.isActive()) {
                throw new BusinessRuleException("INVALID_PAYMENT_STATUS", 
                    "Cannot apply cancelled, failed, or inactive payments");
            }

            // Calculate total application amount
            BigDecimal totalApplicationAmount = request.getApplications().stream()
                    .map(PaymentApplicationRequest.ApplicationDetail::getAppliedAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Validate payment has sufficient unapplied amount
            if (!payment.canBeApplied(totalApplicationAmount)) {
                throw new BusinessRuleException("INSUFFICIENT_PAYMENT_AMOUNT",
                    String.format("Payment has insufficient unapplied amount. Available: %s, Requested: %s",
                                payment.getUnappliedAmount(), totalApplicationAmount));
            }

            List<PaymentApplication> applications = new ArrayList<>();

            // Process each application
            for (PaymentApplicationRequest.ApplicationDetail detail : request.getApplications()) {
                LedgerEntry ledgerEntry = ledgerEntryRepository.findById(detail.getLedgerEntryId())
                        .orElseThrow(() -> new RuntimeException("Ledger entry not found with id: " + detail.getLedgerEntryId()));

                // Validate ledger entry belongs to same customer
                if (!ledgerEntry.getCustomer().getId().equals(payment.getCustomer().getId())) {
                    throw new BusinessRuleException("CUSTOMER_MISMATCH",
                        "Ledger entry must belong to the same customer as the payment");
                }

                // Validate ledger entry is active and a debit
                if (!ledgerEntry.isActive()) {
                    throw new BusinessRuleException("INACTIVE_LEDGER_ENTRY",
                        "Cannot apply payment to inactive ledger entry");
                }

                if (!ledgerEntry.isDebit()) {
                    throw new BusinessRuleException("INVALID_LEDGER_ENTRY_TYPE",
                        "Can only apply payments to debit entries");
                }

                // Create payment application
                PaymentApplication application = new PaymentApplication();
                application.setPayment(payment);
                application.setLedgerEntry(ledgerEntry);
                application.setAppliedAmount(detail.getAppliedAmount());
                application.setApplicationNotes(detail.getApplicationNotes());
                application.setAppliedBy(currentUser);

                applications.add(paymentApplicationRepository.save(application));
            }

            // Update payment applied amount and status
            payment.applyAmount(totalApplicationAmount);
            Payment updatedPayment = paymentRepository.save(payment);

            // Log successful application
            auditService.logSuccess("APPLY_PAYMENT", "PAYMENT", updatedPayment.getId(),
                    null, auditService.createAuditSnapshot(updatedPayment),
                    String.format("Applied payment %d to %d ledger entries, amount: %s", 
                                updatedPayment.getId(), applications.size(), totalApplicationAmount),
                    currentUser);

            return convertToPaymentResponse(updatedPayment);

        } catch (BusinessRuleException e) {
            throw e;
        } catch (Exception e) {
            auditService.logFailure("APPLY_PAYMENT", "PAYMENT", request.getPaymentId(),
                    e.getMessage(),
                    String.format("Failed to apply payment %d", request.getPaymentId()),
                    currentUser);
            throw e;
        }
    }

    /**
     * Auto-apply payment to oldest outstanding entries
     */
    public PaymentResponse autoApplyPayment(Long paymentId) {
        User currentUser = getCurrentUser();

        try {
            Payment payment = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new RuntimeException("Payment not found with id: " + paymentId));

            if (payment.isFullyApplied()) {
                throw new BusinessRuleException("PAYMENT_FULLY_APPLIED",
                    "Payment is already fully applied");
            }

            // Get outstanding debit entries for the customer
            List<LedgerEntry> outstandingEntries = ledgerEntryRepository.findByCustomerIdAndIsActiveTrueOrderByTransactionDateDesc(
                    payment.getCustomer().getId()).stream()
                    .filter(entry -> entry.isDebit() && !entry.isReconciled())
                    .sorted((e1, e2) -> e1.getTransactionDate().compareTo(e2.getTransactionDate())) // Oldest first
                    .collect(Collectors.toList());

            if (outstandingEntries.isEmpty()) {
                throw new BusinessRuleException("NO_OUTSTANDING_ENTRIES",
                    "No outstanding entries found for auto-application");
            }

            List<PaymentApplicationRequest.ApplicationDetail> applications = new ArrayList<>();
            BigDecimal remainingAmount = payment.getUnappliedAmount();

            // Apply to oldest entries first
            for (LedgerEntry entry : outstandingEntries) {
                if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                    break;
                }

                BigDecimal applicationAmount = remainingAmount.min(entry.getAmount());
                applications.add(new PaymentApplicationRequest.ApplicationDetail(
                        entry.getId(), applicationAmount));

                remainingAmount = remainingAmount.subtract(applicationAmount);
            }

            if (applications.isEmpty()) {
                throw new BusinessRuleException("NO_APPLICABLE_AMOUNT",
                    "No amount could be applied to outstanding entries");
            }

            // Create application request and apply
            PaymentApplicationRequest request = new PaymentApplicationRequest();
            request.setPaymentId(paymentId);
            request.setApplications(applications);
            request.setNotes("Auto-applied to oldest outstanding entries");

            return applyPaymentToEntries(request);

        } catch (BusinessRuleException e) {
            throw e;
        } catch (Exception e) {
            auditService.logFailure("AUTO_APPLY_PAYMENT", "PAYMENT", paymentId,
                    e.getMessage(),
                    String.format("Failed to auto-apply payment %d", paymentId),
                    currentUser);
            throw e;
        }
    }

    /**
     * Reverse payment application
     */
    public void reversePaymentApplication(Long applicationId) {
        User currentUser = getCurrentUser();

        try {
            PaymentApplication application = paymentApplicationRepository.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Payment application not found with id: " + applicationId));

            if (application.isReversed()) {
                throw new BusinessRuleException("APPLICATION_ALREADY_REVERSED",
                    "Payment application is already reversed");
            }

            // Create audit snapshot before reversal
            Map<String, Object> oldValues = auditService.createAuditSnapshot(application);

            // Reverse the application
            application.reverse(currentUser);
            paymentApplicationRepository.save(application);

            // Update payment applied amount
            Payment payment = application.getPayment();
            payment.reverseApplication(application.getAppliedAmount());
            paymentRepository.save(payment);

            // Log successful reversal
            auditService.logSuccess("REVERSE_PAYMENT_APPLICATION", "PAYMENT_APPLICATION", applicationId,
                    oldValues, auditService.createAuditSnapshot(application),
                    String.format("Reversed payment application %d, amount: %s", 
                                applicationId, application.getAppliedAmount()),
                    currentUser);

        } catch (BusinessRuleException e) {
            throw e;
        } catch (Exception e) {
            auditService.logFailure("REVERSE_PAYMENT_APPLICATION", "PAYMENT_APPLICATION", applicationId,
                    e.getMessage(),
                    String.format("Failed to reverse payment application %d", applicationId),
                    currentUser);
            throw e;
        }
    }

    /**
     * Get outstanding balance for a customer
     */
    public OutstandingBalanceResponse getOutstandingBalance(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        // Get all active debit entries that are not fully paid
        List<LedgerEntry> debitEntries = ledgerEntryRepository.findByCustomerIdAndIsActiveTrueOrderByTransactionDateDesc(customerId)
                .stream()
                .filter(entry -> entry.isDebit())
                .collect(Collectors.toList());

        // Get all unapplied payments
        List<Payment> unappliedPayments = paymentRepository.findUnappliedPaymentsByCustomer(customerId);

        // Calculate outstanding entries
        List<OutstandingBalanceResponse.OutstandingEntry> outstandingEntries = new ArrayList<>();
        BigDecimal totalOutstanding = BigDecimal.ZERO;
        LocalDate oldestDate = null;
        int totalDaysOutstanding = 0;

        for (LedgerEntry entry : debitEntries) {
            BigDecimal appliedToEntry = paymentApplicationRepository.calculateTotalAppliedForLedgerEntry(entry.getId());
            BigDecimal outstandingAmount = entry.getAmount().subtract(appliedToEntry);

            if (outstandingAmount.compareTo(BigDecimal.ZERO) > 0) {
                int daysOutstanding = (int) ChronoUnit.DAYS.between(entry.getTransactionDate(), LocalDate.now());
                
                outstandingEntries.add(new OutstandingBalanceResponse.OutstandingEntry(
                        entry.getId(),
                        entry.getTransactionDate(),
                        entry.getDescription(),
                        entry.getAmount(),
                        outstandingAmount,
                        daysOutstanding
                ));

                totalOutstanding = totalOutstanding.add(outstandingAmount);
                totalDaysOutstanding += daysOutstanding;

                if (oldestDate == null || entry.getTransactionDate().isBefore(oldestDate)) {
                    oldestDate = entry.getTransactionDate();
                }
            }
        }

        // Calculate unapplied payments
        List<OutstandingBalanceResponse.UnappliedPayment> unappliedPaymentList = new ArrayList<>();
        BigDecimal totalUnapplied = BigDecimal.ZERO;

        for (Payment payment : unappliedPayments) {
            BigDecimal unappliedAmount = payment.getUnappliedAmount();
            if (unappliedAmount.compareTo(BigDecimal.ZERO) > 0) {
                unappliedPaymentList.add(new OutstandingBalanceResponse.UnappliedPayment(
                        payment.getId(),
                        payment.getPaymentDate(),
                        payment.getDescription(),
                        payment.getAmount(),
                        payment.getAppliedAmount(),
                        unappliedAmount,
                        payment.getPaymentMethod()
                ));

                totalUnapplied = totalUnapplied.add(unappliedAmount);
            }
        }

        // Build response
        OutstandingBalanceResponse response = new OutstandingBalanceResponse();
        response.setCustomerId(customerId);
        response.setCustomerName(customer.getName());
        response.setTotalOutstandingBalance(totalOutstanding);
        response.setTotalCurrentBalance(customer.getCurrentBalance());
        response.setTotalUnappliedPayments(totalUnapplied);
        response.setNetOutstandingBalance(totalOutstanding.subtract(totalUnapplied));
        response.setTotalOutstandingEntries(outstandingEntries.size());
        response.setTotalUnappliedPaymentCount(unappliedPaymentList.size());
        response.setOldestOutstandingDate(oldestDate);
        response.setAverageDaysOutstanding(outstandingEntries.isEmpty() ? 0 : totalDaysOutstanding / outstandingEntries.size());
        response.setOutstandingEntries(outstandingEntries);
        response.setUnappliedPayments(unappliedPaymentList);

        return response;
    }

    /**
     * Get payment by ID
     */
    public PaymentResponse getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found with id: " + id));
        return convertToPaymentResponse(payment);
    }

    /**
     * Get payments for customer with pagination
     */
    public Page<PaymentResponse> getPaymentsForCustomer(Long customerId, int page, int size, 
                                                       String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Payment> payments = paymentRepository.findByCustomerIdAndIsActiveTrueOrderByPaymentDateDesc(
                customerId, pageable);
        
        return payments.map(this::convertToPaymentResponse);
    }

    /**
     * Get unapplied payments for customer
     */
    public List<PaymentResponse> getUnappliedPayments(Long customerId) {
        List<Payment> payments = paymentRepository.findUnappliedPaymentsByCustomer(customerId);
        return payments.stream()
                .map(this::convertToPaymentResponse)
                .collect(Collectors.toList());
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

        // Get applications for this payment
        List<PaymentApplication> applications = paymentApplicationRepository.findByPaymentIdAndIsReversedFalseOrderByAppliedAtDesc(payment.getId());
        response.setApplications(applications.stream()
                .map(this::convertToApplicationResponse)
                .collect(Collectors.toList()));

        return response;
    }

    /**
     * Convert PaymentApplication to PaymentApplicationResponse
     */
    private PaymentApplicationResponse convertToApplicationResponse(PaymentApplication application) {
        PaymentApplicationResponse response = new PaymentApplicationResponse();
        response.setId(application.getId());
        response.setAppliedAmount(application.getAppliedAmount());
        response.setApplicationNotes(application.getApplicationNotes());
        response.setReversed(application.isReversed());
        response.setReversedAt(application.getReversedAt());
        response.setReversedByUsername(application.getReversedBy() != null ? application.getReversedBy().getUsername() : null);
        response.setPaymentId(application.getPayment().getId());
        response.setPaymentDescription(application.getPayment().getDescription());
        response.setLedgerEntryId(application.getLedgerEntry().getId());
        response.setLedgerEntryDescription(application.getLedgerEntry().getDescription());
        response.setLedgerEntryAmount(application.getLedgerEntry().getAmount());
        response.setAppliedByUsername(application.getAppliedBy().getUsername());
        response.setAppliedAt(application.getAppliedAt());
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
}
