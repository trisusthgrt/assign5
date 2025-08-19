package com.example.ledgerly.dto;

import com.example.ledgerly.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for payment response data
 */
public class PaymentResponse {

    private Long id;
    private LocalDate paymentDate;
    private BigDecimal amount;
    private BigDecimal appliedAmount;
    private BigDecimal remainingAmount;
    private String description;
    private String notes;
    private String referenceNumber;
    private String paymentMethod;
    private PaymentStatus status;
    private String bankDetails;
    private String checkNumber;
    private LocalDate processedDate;
    private LocalDate dueDate;
    private LocalDateTime statusUpdatedAt;
    private String statusUpdatedByUsername;
    private String statusNotes;
    private LocalDate disputeDate;
    private String disputeReason;
    private String disputedByUsername;
    private Integer overdueDays;
    private LocalDate lastReminderSent;
    private Integer reminderCount;
    private boolean isAdvancePayment;
    private boolean isActive;
    
    // Customer information
    private Long customerId;
    private String customerName;
    
    // User information
    private String createdByUsername;
    private String updatedByUsername;
    
    // Audit information
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Applications
    private List<PaymentApplicationResponse> applications;

    // Constructors
    public PaymentResponse() {
    }

    public PaymentResponse(Long id, LocalDate paymentDate, BigDecimal amount, 
                          PaymentStatus status, Long customerId, String customerName) {
        this.id = id;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.status = status;
        this.customerId = customerId;
        this.customerName = customerName;
    }

    // Helper methods
    public BigDecimal getUnappliedAmount() {
        if (amount != null && appliedAmount != null) {
            return amount.subtract(appliedAmount);
        }
        return amount;
    }

    public boolean isFullyApplied() {
        return appliedAmount != null && amount != null && 
               appliedAmount.compareTo(amount) >= 0;
    }

    public double getApplicationPercentage() {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        if (appliedAmount == null) {
            return 0.0;
        }
        return appliedAmount.divide(amount, 4, java.math.RoundingMode.HALF_UP)
                          .multiply(BigDecimal.valueOf(100))
                          .doubleValue();
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
    }

    public BigDecimal getAppliedAmount() {
        return appliedAmount;
    }

    public void setAppliedAmount(BigDecimal appliedAmount) {
        this.appliedAmount = appliedAmount;
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

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCreatedByUsername() {
        return createdByUsername;
    }

    public void setCreatedByUsername(String createdByUsername) {
        this.createdByUsername = createdByUsername;
    }

    public String getUpdatedByUsername() {
        return updatedByUsername;
    }

    public void setUpdatedByUsername(String updatedByUsername) {
        this.updatedByUsername = updatedByUsername;
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

    public List<PaymentApplicationResponse> getApplications() {
        return applications;
    }

    public void setApplications(List<PaymentApplicationResponse> applications) {
        this.applications = applications;
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

    public String getStatusUpdatedByUsername() {
        return statusUpdatedByUsername;
    }

    public void setStatusUpdatedByUsername(String statusUpdatedByUsername) {
        this.statusUpdatedByUsername = statusUpdatedByUsername;
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

    public String getDisputedByUsername() {
        return disputedByUsername;
    }

    public void setDisputedByUsername(String disputedByUsername) {
        this.disputedByUsername = disputedByUsername;
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

    // Status checking helper methods
    public boolean isOverdue() {
        return status == PaymentStatus.OVERDUE;
    }

    public boolean isDisputed() {
        return status == PaymentStatus.DISPUTED;
    }

    public boolean isPaid() {
        return status == PaymentStatus.PAID;
    }

    public boolean isPending() {
        return status == PaymentStatus.PENDING;
    }

    public boolean isProblematic() {
        return status != null && status.isProblematic();
    }

    public boolean canBeUpdated() {
        return status != null && status.canBeUpdated();
    }
}
