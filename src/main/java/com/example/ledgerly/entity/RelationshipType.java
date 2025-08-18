package com.example.ledgerly.entity;

/**
 * Enum representing different types of customer relationships
 */
public enum RelationshipType {
    CUSTOMER("Customer"),
    SUPPLIER("Supplier"), 
    VENDOR("Vendor"),
    PARTNER("Business Partner"),
    CONTRACTOR("Contractor"),
    FREELANCER("Freelancer"),
    OTHER("Other");

    private final String displayName;

    RelationshipType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
