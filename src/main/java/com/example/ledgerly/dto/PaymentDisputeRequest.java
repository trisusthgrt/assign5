package com.example.ledgerly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for disputing a payment
 */
public class PaymentDisputeRequest {

    @NotBlank(message = "Dispute reason is required")
    @Size(min = 10, max = 1000, message = "Dispute reason must be between 10 and 1000 characters")
    private String disputeReason;

    @Size(max = 1000, message = "Additional notes cannot exceed 1000 characters")
    private String additionalNotes;

    // Constructors
    public PaymentDisputeRequest() {
    }

    public PaymentDisputeRequest(String disputeReason) {
        this.disputeReason = disputeReason;
    }

    // Getters and Setters
    public String getDisputeReason() {
        return disputeReason;
    }

    public void setDisputeReason(String disputeReason) {
        this.disputeReason = disputeReason;
    }

    public String getAdditionalNotes() {
        return additionalNotes;
    }

    public void setAdditionalNotes(String additionalNotes) {
        this.additionalNotes = additionalNotes;
    }
}
