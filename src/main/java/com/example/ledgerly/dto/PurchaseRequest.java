package com.example.ledgerly.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for recording customer purchases (creates DEBIT ledger entries)
 */
public class PurchaseRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Shop ID is required")
    private Long shopId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 500, message = "Description must be between 3 and 500 characters")
    private String description;

    private String category; // Optional field

    @NotNull(message = "Transaction date is required")
    private LocalDate transactionDate;

    private String notes;

    // Constructors
    public PurchaseRequest() {
    }

    public PurchaseRequest(Long customerId, Long shopId, BigDecimal amount, String description, 
                         String category, LocalDate transactionDate, String notes) {
        this.customerId = customerId;
        this.shopId = shopId;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.transactionDate = transactionDate;
        this.notes = notes;
    }

    // Getters and Setters
    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "PurchaseRequest{" +
                "customerId=" + customerId +
                ", shopId=" + shopId +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", transactionDate=" + transactionDate +
                ", notes='" + notes + '\'' +
                '}';
    }
}
