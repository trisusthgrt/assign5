package com.example.ledgerly.exception;

import java.math.BigDecimal;

/**
 * Exception thrown when transaction amount is invalid
 */
public class InvalidTransactionAmountException extends BusinessRuleException {

    private final BigDecimal amount;
    private final String validationRule;

    public InvalidTransactionAmountException(BigDecimal amount, String validationRule, String message) {
        super("INVALID_AMOUNT", message);
        this.amount = amount;
        this.validationRule = validationRule;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getValidationRule() {
        return validationRule;
    }
}
