package com.example.ledgerly.exception;

/**
 * Exception thrown when business rules are violated
 */
public class BusinessRuleException extends RuntimeException {

    private final String ruleCode;
    private final String ruleDescription;

    public BusinessRuleException(String ruleCode, String message) {
        super(message);
        this.ruleCode = ruleCode;
        this.ruleDescription = message;
    }

    public BusinessRuleException(String ruleCode, String message, Throwable cause) {
        super(message, cause);
        this.ruleCode = ruleCode;
        this.ruleDescription = message;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public String getRuleDescription() {
        return ruleDescription;
    }

    @Override
    public String toString() {
        return "BusinessRuleException{" +
                "ruleCode='" + ruleCode + '\'' +
                ", ruleDescription='" + ruleDescription + '\'' +
                '}';
    }
}
