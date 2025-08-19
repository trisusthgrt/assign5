package com.example.ledgerly.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for payment application response data
 */
public class PaymentApplicationResponse {

    private Long id;
    private BigDecimal appliedAmount;
    private String applicationNotes;
    private boolean isReversed;
    private LocalDateTime reversedAt;
    private String reversedByUsername;
    
    // Payment information
    private Long paymentId;
    private String paymentDescription;
    
    // Ledger entry information
    private Long ledgerEntryId;
    private String ledgerEntryDescription;
    private BigDecimal ledgerEntryAmount;
    
    // User information
    private String appliedByUsername;
    
    // Audit information
    private LocalDateTime appliedAt;

    // Constructors
    public PaymentApplicationResponse() {
    }

    public PaymentApplicationResponse(Long id, BigDecimal appliedAmount, 
                                    Long paymentId, Long ledgerEntryId) {
        this.id = id;
        this.appliedAmount = appliedAmount;
        this.paymentId = paymentId;
        this.ledgerEntryId = ledgerEntryId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAppliedAmount() {
        return appliedAmount;
    }

    public void setAppliedAmount(BigDecimal appliedAmount) {
        this.appliedAmount = appliedAmount;
    }

    public String getApplicationNotes() {
        return applicationNotes;
    }

    public void setApplicationNotes(String applicationNotes) {
        this.applicationNotes = applicationNotes;
    }

    public boolean isReversed() {
        return isReversed;
    }

    public void setReversed(boolean reversed) {
        isReversed = reversed;
    }

    public LocalDateTime getReversedAt() {
        return reversedAt;
    }

    public void setReversedAt(LocalDateTime reversedAt) {
        this.reversedAt = reversedAt;
    }

    public String getReversedByUsername() {
        return reversedByUsername;
    }

    public void setReversedByUsername(String reversedByUsername) {
        this.reversedByUsername = reversedByUsername;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentDescription() {
        return paymentDescription;
    }

    public void setPaymentDescription(String paymentDescription) {
        this.paymentDescription = paymentDescription;
    }

    public Long getLedgerEntryId() {
        return ledgerEntryId;
    }

    public void setLedgerEntryId(Long ledgerEntryId) {
        this.ledgerEntryId = ledgerEntryId;
    }

    public String getLedgerEntryDescription() {
        return ledgerEntryDescription;
    }

    public void setLedgerEntryDescription(String ledgerEntryDescription) {
        this.ledgerEntryDescription = ledgerEntryDescription;
    }

    public BigDecimal getLedgerEntryAmount() {
        return ledgerEntryAmount;
    }

    public void setLedgerEntryAmount(BigDecimal ledgerEntryAmount) {
        this.ledgerEntryAmount = ledgerEntryAmount;
    }

    public String getAppliedByUsername() {
        return appliedByUsername;
    }

    public void setAppliedByUsername(String appliedByUsername) {
        this.appliedByUsername = appliedByUsername;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
}
