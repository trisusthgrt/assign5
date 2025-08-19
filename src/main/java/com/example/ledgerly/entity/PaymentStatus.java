package com.example.ledgerly.entity;

/**
 * Enum representing different payment statuses with comprehensive tracking
 */
public enum PaymentStatus {
    // Basic payment statuses
    PENDING("Pending", "Payment recorded but not yet processed", PaymentCategory.WAITING),
    PAID("Paid", "Payment has been successfully completed", PaymentCategory.COMPLETED),
    OVERDUE("Overdue", "Payment is past due date", PaymentCategory.PROBLEMATIC),
    DISPUTED("Disputed", "Payment is under dispute", PaymentCategory.PROBLEMATIC),
    
    // Advanced payment statuses
    PROCESSED("Processed", "Payment has been successfully processed", PaymentCategory.COMPLETED),
    CANCELLED("Cancelled", "Payment has been cancelled", PaymentCategory.COMPLETED),
    FAILED("Failed", "Payment processing failed", PaymentCategory.PROBLEMATIC),
    PARTIAL("Partial", "Partial payment received", PaymentCategory.WAITING),
    REFUNDED("Refunded", "Payment has been refunded", PaymentCategory.COMPLETED),
    
    // Collection statuses
    IN_COLLECTION("In Collection", "Payment sent to collection agency", PaymentCategory.PROBLEMATIC),
    WRITTEN_OFF("Written Off", "Payment written off as bad debt", PaymentCategory.COMPLETED);

    private final String displayName;
    private final String description;
    private final PaymentCategory category;

    PaymentStatus(String displayName, String description, PaymentCategory category) {
        this.displayName = displayName;
        this.description = description;
        this.category = category;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public PaymentCategory getCategory() {
        return category;
    }

    public boolean isCompleted() {
        return category == PaymentCategory.COMPLETED;
    }

    public boolean isProblematic() {
        return category == PaymentCategory.PROBLEMATIC;
    }

    public boolean isWaiting() {
        return category == PaymentCategory.WAITING;
    }

    public boolean canBeUpdated() {
        return this == PENDING || this == PARTIAL || this == DISPUTED;
    }

    public boolean canBeDisputed() {
        return this == PENDING || this == PARTIAL || this == OVERDUE;
    }

    public boolean canBePaid() {
        return this == PENDING || this == PARTIAL || this == OVERDUE || this == DISPUTED;
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Category classification for payment statuses
     */
    public enum PaymentCategory {
        WAITING("Waiting for action"),
        COMPLETED("Payment completed"),
        PROBLEMATIC("Requires attention");

        private final String description;

        PaymentCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
