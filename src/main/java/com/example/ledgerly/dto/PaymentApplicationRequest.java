package com.example.ledgerly.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for applying payment to ledger entries
 */
public class PaymentApplicationRequest {

    @NotNull(message = "Payment ID is required")
    private Long paymentId;

    @NotNull(message = "Applications list is required")
    private List<ApplicationDetail> applications;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    // Constructors
    public PaymentApplicationRequest() {
    }

    public PaymentApplicationRequest(Long paymentId, List<ApplicationDetail> applications) {
        this.paymentId = paymentId;
        this.applications = applications;
    }

    // Getters and Setters
    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public List<ApplicationDetail> getApplications() {
        return applications;
    }

    public void setApplications(List<ApplicationDetail> applications) {
        this.applications = applications;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Inner class for application details
     */
    public static class ApplicationDetail {
        @NotNull(message = "Ledger entry ID is required")
        private Long ledgerEntryId;

        @NotNull(message = "Applied amount is required")
        @DecimalMin(value = "0.01", message = "Applied amount must be greater than 0")
        private BigDecimal appliedAmount;

        @Size(max = 500, message = "Application notes cannot exceed 500 characters")
        private String applicationNotes;

        // Constructors
        public ApplicationDetail() {
        }

        public ApplicationDetail(Long ledgerEntryId, BigDecimal appliedAmount) {
            this.ledgerEntryId = ledgerEntryId;
            this.appliedAmount = appliedAmount;
        }

        // Getters and Setters
        public Long getLedgerEntryId() {
            return ledgerEntryId;
        }

        public void setLedgerEntryId(Long ledgerEntryId) {
            this.ledgerEntryId = ledgerEntryId;
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
    }
}
