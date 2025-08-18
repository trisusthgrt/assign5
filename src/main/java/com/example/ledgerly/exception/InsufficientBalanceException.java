package com.example.ledgerly.exception;

import java.math.BigDecimal;

/**
 * Exception thrown when a transaction would result in negative balance
 */
public class InsufficientBalanceException extends BusinessRuleException {

    private final BigDecimal currentBalance;
    private final BigDecimal requestedAmount;
    private final BigDecimal resultingBalance;

    public InsufficientBalanceException(BigDecimal currentBalance, BigDecimal requestedAmount, BigDecimal resultingBalance) {
        super("INSUFFICIENT_BALANCE", 
              String.format("Transaction would result in negative balance. Current: %s, Requested: %s, Resulting: %s", 
                          currentBalance, requestedAmount, resultingBalance));
        this.currentBalance = currentBalance;
        this.requestedAmount = requestedAmount;
        this.resultingBalance = resultingBalance;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public BigDecimal getResultingBalance() {
        return resultingBalance;
    }
}
