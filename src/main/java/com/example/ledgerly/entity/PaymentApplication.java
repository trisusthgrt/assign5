package com.example.ledgerly.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_applications")
public class PaymentApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = "Applied amount is required")
    @DecimalMin(value = "0.01", message = "Applied amount must be greater than 0")
    @Column(name = "applied_amount", precision = 15, scale = 2, nullable = false)
    private BigDecimal appliedAmount;

    @Column(name = "application_notes")
    private String applicationNotes;

    @Column(name = "is_reversed", nullable = false)
    private boolean isReversed = false;

    @Column(name = "reversed_at")
    private LocalDateTime reversedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reversed_by_user_id")
    private User reversedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ledger_entry_id", nullable = false)
    private LedgerEntry ledgerEntry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applied_by_user_id", nullable = false)
    private User appliedBy;

    @CreationTimestamp
    @Column(name = "applied_at", nullable = false, updatable = false)
    private LocalDateTime appliedAt;

    public PaymentApplication() {
    }

    public PaymentApplication(Payment payment, LedgerEntry ledgerEntry, 
                            BigDecimal appliedAmount, User appliedBy) {
        this.payment = payment;
        this.ledgerEntry = ledgerEntry;
        this.appliedAmount = appliedAmount;
        this.appliedBy = appliedBy;
    }

    public void reverse(User reversedBy) {
        this.isReversed = true;
        this.reversedAt = LocalDateTime.now();
        this.reversedBy = reversedBy;
    }

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

    public User getReversedBy() {
        return reversedBy;
    }

    public void setReversedBy(User reversedBy) {
        this.reversedBy = reversedBy;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public LedgerEntry getLedgerEntry() {
        return ledgerEntry;
    }

    public void setLedgerEntry(LedgerEntry ledgerEntry) {
        this.ledgerEntry = ledgerEntry;
    }

    public User getAppliedBy() {
        return appliedBy;
    }

    public void setAppliedBy(User appliedBy) {
        this.appliedBy = appliedBy;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    @Override
    public String toString() {
        return "PaymentApplication{" +
                "id=" + id +
                ", appliedAmount=" + appliedAmount +
                ", isReversed=" + isReversed +
                ", payment=" + (payment != null ? payment.getId() : "null") +
                ", ledgerEntry=" + (ledgerEntry != null ? ledgerEntry.getId() : "null") +
                '}';
    }
}
