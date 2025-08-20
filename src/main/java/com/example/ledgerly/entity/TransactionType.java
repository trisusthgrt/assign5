package com.example.ledgerly.entity;

public enum TransactionType {
    CREDIT("Credit", "Money received/incoming"),
    DEBIT("Debit", "Money paid/outgoing"),
    OPENING_BALANCE("Opening Balance", "Initial balance setup"),
    ADJUSTMENT("Adjustment", "Manual adjustment entry"),
    TRANSFER("Transfer", "Transfer between accounts");

    private final String displayName;
    private final String description;

    TransactionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
