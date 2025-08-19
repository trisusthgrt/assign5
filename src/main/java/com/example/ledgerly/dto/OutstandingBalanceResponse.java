package com.example.ledgerly.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for outstanding balance response data
 */
public class OutstandingBalanceResponse {

    private Long customerId;
    private String customerName;
    private BigDecimal totalOutstandingBalance;
    private BigDecimal totalCurrentBalance;
    private BigDecimal totalUnappliedPayments;
    private BigDecimal netOutstandingBalance;
    private long totalOutstandingEntries;
    private long totalUnappliedPaymentCount;
    private LocalDate oldestOutstandingDate;
    private int averageDaysOutstanding;
    
    private List<OutstandingEntry> outstandingEntries;
    private List<UnappliedPayment> unappliedPayments;

    // Constructors
    public OutstandingBalanceResponse() {
    }

    public OutstandingBalanceResponse(Long customerId, String customerName, 
                                    BigDecimal totalOutstandingBalance) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.totalOutstandingBalance = totalOutstandingBalance;
    }

    // Helper methods
    public boolean hasOutstandingBalance() {
        return totalOutstandingBalance != null && 
               totalOutstandingBalance.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasUnappliedPayments() {
        return totalUnappliedPayments != null && 
               totalUnappliedPayments.compareTo(BigDecimal.ZERO) > 0;
    }

    // Getters and Setters
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

    public BigDecimal getTotalOutstandingBalance() {
        return totalOutstandingBalance;
    }

    public void setTotalOutstandingBalance(BigDecimal totalOutstandingBalance) {
        this.totalOutstandingBalance = totalOutstandingBalance;
    }

    public BigDecimal getTotalCurrentBalance() {
        return totalCurrentBalance;
    }

    public void setTotalCurrentBalance(BigDecimal totalCurrentBalance) {
        this.totalCurrentBalance = totalCurrentBalance;
    }

    public BigDecimal getTotalUnappliedPayments() {
        return totalUnappliedPayments;
    }

    public void setTotalUnappliedPayments(BigDecimal totalUnappliedPayments) {
        this.totalUnappliedPayments = totalUnappliedPayments;
    }

    public BigDecimal getNetOutstandingBalance() {
        return netOutstandingBalance;
    }

    public void setNetOutstandingBalance(BigDecimal netOutstandingBalance) {
        this.netOutstandingBalance = netOutstandingBalance;
    }

    public long getTotalOutstandingEntries() {
        return totalOutstandingEntries;
    }

    public void setTotalOutstandingEntries(long totalOutstandingEntries) {
        this.totalOutstandingEntries = totalOutstandingEntries;
    }

    public long getTotalUnappliedPaymentCount() {
        return totalUnappliedPaymentCount;
    }

    public void setTotalUnappliedPaymentCount(long totalUnappliedPaymentCount) {
        this.totalUnappliedPaymentCount = totalUnappliedPaymentCount;
    }

    public LocalDate getOldestOutstandingDate() {
        return oldestOutstandingDate;
    }

    public void setOldestOutstandingDate(LocalDate oldestOutstandingDate) {
        this.oldestOutstandingDate = oldestOutstandingDate;
    }

    public int getAverageDaysOutstanding() {
        return averageDaysOutstanding;
    }

    public void setAverageDaysOutstanding(int averageDaysOutstanding) {
        this.averageDaysOutstanding = averageDaysOutstanding;
    }

    public List<OutstandingEntry> getOutstandingEntries() {
        return outstandingEntries;
    }

    public void setOutstandingEntries(List<OutstandingEntry> outstandingEntries) {
        this.outstandingEntries = outstandingEntries;
    }

    public List<UnappliedPayment> getUnappliedPayments() {
        return unappliedPayments;
    }

    public void setUnappliedPayments(List<UnappliedPayment> unappliedPayments) {
        this.unappliedPayments = unappliedPayments;
    }

    /**
     * Inner class for outstanding entry details
     */
    public static class OutstandingEntry {
        private Long ledgerEntryId;
        private LocalDate transactionDate;
        private String description;
        private BigDecimal originalAmount;
        private BigDecimal outstandingAmount;
        private int daysOutstanding;

        public OutstandingEntry() {
        }

        public OutstandingEntry(Long ledgerEntryId, LocalDate transactionDate, 
                              String description, BigDecimal originalAmount, 
                              BigDecimal outstandingAmount, int daysOutstanding) {
            this.ledgerEntryId = ledgerEntryId;
            this.transactionDate = transactionDate;
            this.description = description;
            this.originalAmount = originalAmount;
            this.outstandingAmount = outstandingAmount;
            this.daysOutstanding = daysOutstanding;
        }

        // Getters and Setters
        public Long getLedgerEntryId() { return ledgerEntryId; }
        public void setLedgerEntryId(Long ledgerEntryId) { this.ledgerEntryId = ledgerEntryId; }
        
        public LocalDate getTransactionDate() { return transactionDate; }
        public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public BigDecimal getOriginalAmount() { return originalAmount; }
        public void setOriginalAmount(BigDecimal originalAmount) { this.originalAmount = originalAmount; }
        
        public BigDecimal getOutstandingAmount() { return outstandingAmount; }
        public void setOutstandingAmount(BigDecimal outstandingAmount) { this.outstandingAmount = outstandingAmount; }
        
        public int getDaysOutstanding() { return daysOutstanding; }
        public void setDaysOutstanding(int daysOutstanding) { this.daysOutstanding = daysOutstanding; }
    }

    /**
     * Inner class for unapplied payment details
     */
    public static class UnappliedPayment {
        private Long paymentId;
        private LocalDate paymentDate;
        private String description;
        private BigDecimal totalAmount;
        private BigDecimal appliedAmount;
        private BigDecimal unappliedAmount;
        private String paymentMethod;

        public UnappliedPayment() {
        }

        public UnappliedPayment(Long paymentId, LocalDate paymentDate, String description, 
                              BigDecimal totalAmount, BigDecimal appliedAmount, 
                              BigDecimal unappliedAmount, String paymentMethod) {
            this.paymentId = paymentId;
            this.paymentDate = paymentDate;
            this.description = description;
            this.totalAmount = totalAmount;
            this.appliedAmount = appliedAmount;
            this.unappliedAmount = unappliedAmount;
            this.paymentMethod = paymentMethod;
        }

        // Getters and Setters
        public Long getPaymentId() { return paymentId; }
        public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
        
        public LocalDate getPaymentDate() { return paymentDate; }
        public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public BigDecimal getTotalAmount() { return totalAmount; }
        public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
        
        public BigDecimal getAppliedAmount() { return appliedAmount; }
        public void setAppliedAmount(BigDecimal appliedAmount) { this.appliedAmount = appliedAmount; }
        
        public BigDecimal getUnappliedAmount() { return unappliedAmount; }
        public void setUnappliedAmount(BigDecimal unappliedAmount) { this.unappliedAmount = unappliedAmount; }
        
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }
}
