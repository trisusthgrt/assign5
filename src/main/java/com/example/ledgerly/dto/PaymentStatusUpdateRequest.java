package com.example.ledgerly.dto;

import com.example.ledgerly.entity.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * DTO for updating payment status
 */
public class PaymentStatusUpdateRequest {

    @NotNull(message = "Payment status is required")
    private PaymentStatus status;

    @Size(max = 1000, message = "Status notes cannot exceed 1000 characters")
    private String statusNotes;

    private LocalDate dueDate;

    // Constructors
    public PaymentStatusUpdateRequest() {
    }

    public PaymentStatusUpdateRequest(PaymentStatus status, String statusNotes) {
        this.status = status;
        this.statusNotes = statusNotes;
    }

    // Getters and Setters
    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getStatusNotes() {
        return statusNotes;
    }

    public void setStatusNotes(String statusNotes) {
        this.statusNotes = statusNotes;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
