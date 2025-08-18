package com.example.ledgerly.dto;

import com.example.ledgerly.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for ledger entry response data
 */
public class LedgerEntryResponse {

    private Long id;
    private LocalDate transactionDate;
    private TransactionType transactionType;
    private BigDecimal amount;
    private String description;
    private String notes;
    private String referenceNumber;
    private String invoiceNumber;
    private LocalDate invoiceDate;
    private String paymentMethod;
    private BigDecimal balanceAfterTransaction;
    private boolean isReconciled;
    private LocalDate reconciledDate;
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
    
    // Attachments
    private List<DocumentAttachmentResponse> attachments;

    // Constructors
    public LedgerEntryResponse() {
    }

    public LedgerEntryResponse(Long id, LocalDate transactionDate, TransactionType transactionType, 
                             BigDecimal amount, String description, Long customerId, String customerName) {
        this.id = id;
        this.transactionDate = transactionDate;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.customerId = customerId;
        this.customerName = customerName;
    }

    // Helper methods
    public boolean isCredit() {
        return TransactionType.CREDIT.equals(this.transactionType) || 
               TransactionType.OPENING_BALANCE.equals(this.transactionType);
    }

    public boolean isDebit() {
        return TransactionType.DEBIT.equals(this.transactionType);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getBalanceAfterTransaction() {
        return balanceAfterTransaction;
    }

    public void setBalanceAfterTransaction(BigDecimal balanceAfterTransaction) {
        this.balanceAfterTransaction = balanceAfterTransaction;
    }

    public boolean isReconciled() {
        return isReconciled;
    }

    public void setReconciled(boolean reconciled) {
        isReconciled = reconciled;
    }

    public LocalDate getReconciledDate() {
        return reconciledDate;
    }

    public void setReconciledDate(LocalDate reconciledDate) {
        this.reconciledDate = reconciledDate;
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

    public List<DocumentAttachmentResponse> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<DocumentAttachmentResponse> attachments) {
        this.attachments = attachments;
    }
}
