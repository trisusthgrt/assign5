package com.example.ledgerly.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a payment made by or to a customer
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Payment date is required")
    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @NotNull(message = "Payment amount is required")
    @DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
    @Column(name = "amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @NotNull(message = "Applied amount is required")
    @Column(name = "applied_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal appliedAmount = BigDecimal.ZERO;

    @Column(name = "remaining_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal remainingAmount = BigDecimal.ZERO;

    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 500, message = "Description must be between 3 and 500 characters")
    @Column(name = "description", nullable = false)
    private String description;

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    @Column(name = "notes", length = 1000)
    private String notes;

    @Size(max = 100, message = "Reference number cannot exceed 100 characters")
    @Column(name = "reference_number")
    private String referenceNumber;

    @Size(max = 100, message = "Payment method cannot exceed 100 characters")
    @Column(name = "payment_method")
    private String paymentMethod;

    @NotNull(message = "Payment status is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "bank_details")
    private String bankDetails;

    @Column(name = "check_number")
    private String checkNumber;

    @Column(name = "processed_date")
    private LocalDate processedDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "status_updated_at")
    private LocalDateTime statusUpdatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_updated_by_user_id")
    private User statusUpdatedBy;

    @Size(max = 1000, message = "Status notes cannot exceed 1000 characters")
    @Column(name = "status_notes", length = 1000)
    private String statusNotes;

    @Column(name = "dispute_date")
    private LocalDate disputeDate;

    @Size(max = 1000, message = "Dispute reason cannot exceed 1000 characters")
    @Column(name = "dispute_reason", length = 1000)
    private String disputeReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disputed_by_user_id")
    private User disputedBy;

    @Column(name = "overdue_days")
    private Integer overdueDays = 0;

    @Column(name = "last_reminder_sent")
    private LocalDate lastReminderSent;

    @Column(name = "reminder_count")
    private Integer reminderCount = 0;

    @Column(name = "is_advance_payment", nullable = false)
    private boolean isAdvancePayment = false;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_user_id")
    private User updatedBy;

    // Audit fields
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Payment() {
    }

    public Payment(LocalDate paymentDate, BigDecimal amount, String description, 
                  Customer customer, User createdBy) {
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.remainingAmount = amount;
        this.description = description;
        this.customer = customer;
        this.createdBy = createdBy;
    }

    // Helper methods
    public BigDecimal getUnappliedAmount() {
        return amount.subtract(appliedAmount);
    }

    public boolean isFullyApplied() {
        return appliedAmount.compareTo(amount) >= 0;
    }

    public boolean canBeApplied(BigDecimal applicationAmount) {
        return getUnappliedAmount().compareTo(applicationAmount) >= 0;
    }

    public void applyAmount(BigDecimal applicationAmount) {
        if (!canBeApplied(applicationAmount)) {
            throw new IllegalArgumentException("Cannot apply amount greater than remaining unapplied amount");
        }
        this.appliedAmount = this.appliedAmount.add(applicationAmount);
        this.remainingAmount = this.amount.subtract(this.appliedAmount);
        
        if (isFullyApplied()) {
            this.status = PaymentStatus.PROCESSED;
        } else {
            this.status = PaymentStatus.PARTIAL;
        }
    }

    public void reverseApplication(BigDecimal reversalAmount) {
        if (this.appliedAmount.compareTo(reversalAmount) < 0) {
            throw new IllegalArgumentException("Cannot reverse more than applied amount");
        }
        this.appliedAmount = this.appliedAmount.subtract(reversalAmount);
        this.remainingAmount = this.amount.subtract(this.appliedAmount);
        
        if (this.appliedAmount.compareTo(BigDecimal.ZERO) == 0) {
            this.status = PaymentStatus.PENDING;
        } else {
            this.status = PaymentStatus.PARTIAL;
        }
    }

    // Status management helper methods
    public void updateStatus(PaymentStatus newStatus, User updatedBy, String notes) {
        if (!this.status.canBeUpdated()) {
            throw new IllegalStateException("Payment status cannot be updated from " + this.status);
        }
        this.status = newStatus;
        this.statusUpdatedAt = LocalDateTime.now();
        this.statusUpdatedBy = updatedBy;
        this.statusNotes = notes;
    }

    public void markAsPaid(User updatedBy, String notes) {
        updateStatus(PaymentStatus.PAID, updatedBy, notes);
        this.processedDate = LocalDate.now();
    }

    public void markAsOverdue(User updatedBy, int daysOverdue) {
        updateStatus(PaymentStatus.OVERDUE, updatedBy, "Payment is " + daysOverdue + " days overdue");
        this.overdueDays = daysOverdue;
    }

    public void markAsDisputed(User disputedBy, String disputeReason) {
        if (!this.status.canBeDisputed()) {
            throw new IllegalStateException("Payment cannot be disputed from status " + this.status);
        }
        updateStatus(PaymentStatus.DISPUTED, disputedBy, "Payment disputed");
        this.disputeDate = LocalDate.now();
        this.disputeReason = disputeReason;
        this.disputedBy = disputedBy;
    }

    public void resolveDispute(User resolvedBy, String resolutionNotes) {
        if (this.status != PaymentStatus.DISPUTED) {
            throw new IllegalStateException("Payment is not currently disputed");
        }
        updateStatus(PaymentStatus.PENDING, resolvedBy, "Dispute resolved: " + resolutionNotes);
        this.disputeDate = null;
        this.disputeReason = null;
        this.disputedBy = null;
    }

    public boolean isOverdue() {
        return this.dueDate != null && this.dueDate.isBefore(LocalDate.now()) 
               && (this.status == PaymentStatus.PENDING || this.status == PaymentStatus.PARTIAL);
    }

    public int calculateOverdueDays() {
        if (this.dueDate == null || !this.dueDate.isBefore(LocalDate.now())) {
            return 0;
        }
        return (int) java.time.temporal.ChronoUnit.DAYS.between(this.dueDate, LocalDate.now());
    }

    public void incrementReminderCount() {
        this.reminderCount = (this.reminderCount == null ? 0 : this.reminderCount) + 1;
        this.lastReminderSent = LocalDate.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        if (this.remainingAmount == null || this.appliedAmount == null) {
            this.remainingAmount = amount;
            this.appliedAmount = BigDecimal.ZERO;
        } else {
            this.remainingAmount = amount.subtract(this.appliedAmount);
        }
    }

    public BigDecimal getAppliedAmount() {
        return appliedAmount;
    }

    public void setAppliedAmount(BigDecimal appliedAmount) {
        this.appliedAmount = appliedAmount;
        if (this.amount != null) {
            this.remainingAmount = this.amount.subtract(appliedAmount);
        }
    }

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(String bankDetails) {
        this.bankDetails = bankDetails;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public LocalDate getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(LocalDate processedDate) {
        this.processedDate = processedDate;
    }

    public boolean isAdvancePayment() {
        return isAdvancePayment;
    }

    public void setAdvancePayment(boolean advancePayment) {
        isAdvancePayment = advancePayment;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getStatusUpdatedAt() {
        return statusUpdatedAt;
    }

    public void setStatusUpdatedAt(LocalDateTime statusUpdatedAt) {
        this.statusUpdatedAt = statusUpdatedAt;
    }

    public User getStatusUpdatedBy() {
        return statusUpdatedBy;
    }

    public void setStatusUpdatedBy(User statusUpdatedBy) {
        this.statusUpdatedBy = statusUpdatedBy;
    }

    public String getStatusNotes() {
        return statusNotes;
    }

    public void setStatusNotes(String statusNotes) {
        this.statusNotes = statusNotes;
    }

    public LocalDate getDisputeDate() {
        return disputeDate;
    }

    public void setDisputeDate(LocalDate disputeDate) {
        this.disputeDate = disputeDate;
    }

    public String getDisputeReason() {
        return disputeReason;
    }

    public void setDisputeReason(String disputeReason) {
        this.disputeReason = disputeReason;
    }

    public User getDisputedBy() {
        return disputedBy;
    }

    public void setDisputedBy(User disputedBy) {
        this.disputedBy = disputedBy;
    }

    public Integer getOverdueDays() {
        return overdueDays;
    }

    public void setOverdueDays(Integer overdueDays) {
        this.overdueDays = overdueDays;
    }

    public LocalDate getLastReminderSent() {
        return lastReminderSent;
    }

    public void setLastReminderSent(LocalDate lastReminderSent) {
        this.lastReminderSent = lastReminderSent;
    }

    public Integer getReminderCount() {
        return reminderCount;
    }

    public void setReminderCount(Integer reminderCount) {
        this.reminderCount = reminderCount;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", paymentDate=" + paymentDate +
                ", amount=" + amount +
                ", appliedAmount=" + appliedAmount +
                ", remainingAmount=" + remainingAmount +
                ", status=" + status +
                ", dueDate=" + dueDate +
                ", overdueDays=" + overdueDays +
                ", customer=" + (customer != null ? customer.getName() : "null") +
                ", isActive=" + isActive +
                '}';
    }
}
