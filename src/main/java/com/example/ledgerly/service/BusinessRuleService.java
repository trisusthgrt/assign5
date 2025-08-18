package com.example.ledgerly.service;

import com.example.ledgerly.entity.Customer;
import com.example.ledgerly.entity.LedgerEntry;
import com.example.ledgerly.entity.TransactionType;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.exception.BusinessRuleException;
import com.example.ledgerly.exception.InsufficientBalanceException;
import com.example.ledgerly.exception.InvalidTransactionAmountException;
import com.example.ledgerly.repository.CustomerRepository;
import com.example.ledgerly.repository.LedgerEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service for enforcing business rules across the application
 */
@Service
public class BusinessRuleService {

    private final LedgerEntryRepository ledgerEntryRepository;
    private final CustomerRepository customerRepository;
    private final AuditService auditService;

    // Configuration properties
    @Value("${app.business-rules.allow-negative-balance:false}")
    private boolean allowNegativeBalance;

    @Value("${app.business-rules.max-transaction-amount:1000000}")
    private BigDecimal maxTransactionAmount;

    @Value("${app.business-rules.min-transaction-amount:0.01}")
    private BigDecimal minTransactionAmount;

    @Value("${app.business-rules.max-daily-transaction-limit:100000}")
    private BigDecimal maxDailyTransactionLimit;

    @Value("${app.business-rules.require-future-date-validation:true}")
    private boolean requireFutureDateValidation;

    @Autowired
    public BusinessRuleService(LedgerEntryRepository ledgerEntryRepository,
                             CustomerRepository customerRepository,
                             AuditService auditService) {
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.customerRepository = customerRepository;
        this.auditService = auditService;
    }

    /**
     * Validate a new ledger entry before creation
     */
    public void validateLedgerEntryCreation(LedgerEntry entry, User currentUser) {
        // Basic amount validation
        validateTransactionAmount(entry.getAmount(), currentUser);
        
        // Date validation
        validateTransactionDate(entry.getTransactionDate(), currentUser);
        
        // Customer validation
        validateCustomerStatus(entry.getCustomer(), currentUser);
        
        // Balance validation for debit transactions
        if (entry.isDebit()) {
            validateSufficientBalance(entry.getCustomer(), entry.getAmount(), currentUser);
        }
        
        // Credit limit validation for credit transactions
        if (entry.isCredit()) {
            validateCreditLimit(entry.getCustomer(), entry.getAmount(), currentUser);
        }
        
        // Daily transaction limit validation
        validateDailyTransactionLimit(entry.getCustomer(), entry.getAmount(), entry.getTransactionDate(), currentUser);
    }

    /**
     * Validate a ledger entry update
     */
    public void validateLedgerEntryUpdate(LedgerEntry existingEntry, LedgerEntry updatedEntry, User currentUser) {
        // Amount validation if amount is being changed
        if (updatedEntry.getAmount() != null && !updatedEntry.getAmount().equals(existingEntry.getAmount())) {
            validateTransactionAmount(updatedEntry.getAmount(), currentUser);
            
            // Check balance impact for the difference
            BigDecimal amountDifference = updatedEntry.getAmount().subtract(existingEntry.getAmount());
            if (existingEntry.isDebit() && amountDifference.compareTo(BigDecimal.ZERO) > 0) {
                validateSufficientBalance(existingEntry.getCustomer(), amountDifference, currentUser);
            }
        }
        
        // Date validation if date is being changed
        if (updatedEntry.getTransactionDate() != null) {
            validateTransactionDate(updatedEntry.getTransactionDate(), currentUser);
        }
        
        // Prevent changing reconciled entries
        if (existingEntry.isReconciled() && 
            (updatedEntry.getAmount() != null || updatedEntry.getTransactionType() != null)) {
            throw new BusinessRuleException("RECONCILED_ENTRY_MODIFICATION", 
                "Cannot modify amount or type of reconciled entries");
        }
    }

    /**
     * Validate transaction amount
     */
    public void validateTransactionAmount(BigDecimal amount, User currentUser) {
        if (amount == null) {
            throw new InvalidTransactionAmountException(null, "NULL_AMOUNT", "Transaction amount cannot be null");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            auditService.logBusinessRuleViolation("AMOUNT_VALIDATION", "NEGATIVE_OR_ZERO_AMOUNT", 
                "Amount must be positive: " + amount, "TRANSACTION", null, currentUser);
            throw new InvalidTransactionAmountException(amount, "NEGATIVE_OR_ZERO_AMOUNT", 
                "Transaction amount must be greater than zero");
        }
        
        if (amount.compareTo(minTransactionAmount) < 0) {
            auditService.logBusinessRuleViolation("AMOUNT_VALIDATION", "BELOW_MIN_AMOUNT", 
                "Amount below minimum: " + amount, "TRANSACTION", null, currentUser);
            throw new InvalidTransactionAmountException(amount, "BELOW_MIN_AMOUNT", 
                String.format("Transaction amount must be at least %s", minTransactionAmount));
        }
        
        if (amount.compareTo(maxTransactionAmount) > 0) {
            auditService.logBusinessRuleViolation("AMOUNT_VALIDATION", "ABOVE_MAX_AMOUNT", 
                "Amount above maximum: " + amount, "TRANSACTION", null, currentUser);
            throw new InvalidTransactionAmountException(amount, "ABOVE_MAX_AMOUNT", 
                String.format("Transaction amount cannot exceed %s", maxTransactionAmount));
        }
        
        // Check decimal places (max 2 decimal places)
        if (amount.scale() > 2) {
            throw new InvalidTransactionAmountException(amount, "INVALID_DECIMAL_PLACES", 
                "Transaction amount cannot have more than 2 decimal places");
        }
    }

    /**
     * Validate sufficient balance for debit transactions
     */
    public void validateSufficientBalance(Customer customer, BigDecimal debitAmount, User currentUser) {
        if (!allowNegativeBalance) {
            BigDecimal currentBalance = ledgerEntryRepository.calculateCurrentBalanceForCustomer(customer.getId());
            BigDecimal resultingBalance = currentBalance.subtract(debitAmount);
            
            if (resultingBalance.compareTo(BigDecimal.ZERO) < 0) {
                auditService.logBusinessRuleViolation("BALANCE_VALIDATION", "INSUFFICIENT_BALANCE", 
                    String.format("Insufficient balance. Current: %s, Requested: %s", currentBalance, debitAmount), 
                    "CUSTOMER", customer.getId(), currentUser);
                
                throw new InsufficientBalanceException(currentBalance, debitAmount, resultingBalance);
            }
        }
    }

    /**
     * Validate credit limit
     */
    public void validateCreditLimit(Customer customer, BigDecimal creditAmount, User currentUser) {
        if (customer.getCreditLimit() != null && customer.getCreditLimit().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal currentBalance = ledgerEntryRepository.calculateCurrentBalanceForCustomer(customer.getId());
            BigDecimal newBalance = currentBalance.add(creditAmount);
            
            if (newBalance.compareTo(customer.getCreditLimit()) > 0) {
                auditService.logBusinessRuleViolation("CREDIT_LIMIT_VALIDATION", "CREDIT_LIMIT_EXCEEDED", 
                    String.format("Credit limit exceeded. Limit: %s, New Balance: %s", customer.getCreditLimit(), newBalance), 
                    "CUSTOMER", customer.getId(), currentUser);
                
                throw new BusinessRuleException("CREDIT_LIMIT_EXCEEDED", 
                    String.format("Credit limit of %s would be exceeded. New balance would be %s", 
                                customer.getCreditLimit(), newBalance));
            }
        }
    }

    /**
     * Validate daily transaction limit
     */
    public void validateDailyTransactionLimit(Customer customer, BigDecimal amount, LocalDate transactionDate, User currentUser) {
        // Calculate total transactions for the customer on the given date
        BigDecimal dailyTotal = ledgerEntryRepository.searchLedgerEntries(
            customer.getId(), null, transactionDate, transactionDate, 
            null, null, null, null, null, null, 
            org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
            .getContent()
            .stream()
            .map(LedgerEntry::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal newDailyTotal = dailyTotal.add(amount);
        
        if (newDailyTotal.compareTo(maxDailyTransactionLimit) > 0) {
            auditService.logBusinessRuleViolation("DAILY_LIMIT_VALIDATION", "DAILY_LIMIT_EXCEEDED", 
                String.format("Daily transaction limit exceeded. Limit: %s, Today's Total: %s", 
                            maxDailyTransactionLimit, newDailyTotal), 
                "CUSTOMER", customer.getId(), currentUser);
            
            throw new BusinessRuleException("DAILY_LIMIT_EXCEEDED", 
                String.format("Daily transaction limit of %s would be exceeded. Today's total would be %s", 
                            maxDailyTransactionLimit, newDailyTotal));
        }
    }

    /**
     * Validate transaction date
     */
    public void validateTransactionDate(LocalDate transactionDate, User currentUser) {
        if (transactionDate == null) {
            throw new BusinessRuleException("INVALID_DATE", "Transaction date cannot be null");
        }
        
        // Prevent future dates if configured
        if (requireFutureDateValidation && transactionDate.isAfter(LocalDate.now())) {
            auditService.logBusinessRuleViolation("DATE_VALIDATION", "FUTURE_DATE_NOT_ALLOWED", 
                "Future transaction date not allowed: " + transactionDate, "TRANSACTION", null, currentUser);
            
            throw new BusinessRuleException("FUTURE_DATE_NOT_ALLOWED", 
                "Transaction date cannot be in the future");
        }
        
        // Prevent dates too far in the past (e.g., more than 1 year)
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        if (transactionDate.isBefore(oneYearAgo)) {
            auditService.logBusinessRuleViolation("DATE_VALIDATION", "DATE_TOO_OLD", 
                "Transaction date too old: " + transactionDate, "TRANSACTION", null, currentUser);
            
            throw new BusinessRuleException("DATE_TOO_OLD", 
                "Transaction date cannot be more than 1 year in the past");
        }
    }

    /**
     * Validate customer status
     */
    public void validateCustomerStatus(Customer customer, User currentUser) {
        if (customer == null) {
            throw new BusinessRuleException("INVALID_CUSTOMER", "Customer cannot be null");
        }
        
        if (!customer.isActive()) {
            auditService.logBusinessRuleViolation("CUSTOMER_VALIDATION", "INACTIVE_CUSTOMER", 
                "Cannot create transaction for inactive customer", "CUSTOMER", customer.getId(), currentUser);
            
            throw new BusinessRuleException("INACTIVE_CUSTOMER", 
                "Cannot create transactions for inactive customers");
        }
    }

    /**
     * Validate deletion of ledger entry
     */
    public void validateLedgerEntryDeletion(LedgerEntry entry, User currentUser) {
        if (entry.isReconciled()) {
            auditService.logBusinessRuleViolation("DELETION_VALIDATION", "RECONCILED_ENTRY_DELETION", 
                "Cannot delete reconciled entries", "LEDGER_ENTRY", entry.getId(), currentUser);
            
            throw new BusinessRuleException("RECONCILED_ENTRY_DELETION", 
                "Cannot delete reconciled entries");
        }
        
        // Prevent deletion of entries older than 30 days without admin approval
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        if (entry.getTransactionDate().isBefore(thirtyDaysAgo) && 
            !currentUser.getRole().name().equals("ADMIN")) {
            
            auditService.logBusinessRuleViolation("DELETION_VALIDATION", "OLD_ENTRY_DELETION", 
                "Non-admin cannot delete entries older than 30 days", "LEDGER_ENTRY", entry.getId(), currentUser);
            
            throw new BusinessRuleException("OLD_ENTRY_DELETION", 
                "Only administrators can delete entries older than 30 days");
        }
    }

    /**
     * Check if customer can accept more credit
     */
    public boolean canAcceptCredit(Customer customer, BigDecimal amount) {
        if (customer.getCreditLimit() == null || customer.getCreditLimit().compareTo(BigDecimal.ZERO) <= 0) {
            return true; // No limit set
        }
        
        BigDecimal currentBalance = ledgerEntryRepository.calculateCurrentBalanceForCustomer(customer.getId());
        BigDecimal newBalance = currentBalance.add(amount);
        
        return newBalance.compareTo(customer.getCreditLimit()) <= 0;
    }

    /**
     * Check if customer has sufficient balance for debit
     */
    public boolean hasSufficientBalance(Customer customer, BigDecimal amount) {
        if (allowNegativeBalance) {
            return true;
        }
        
        BigDecimal currentBalance = ledgerEntryRepository.calculateCurrentBalanceForCustomer(customer.getId());
        return currentBalance.compareTo(amount) >= 0;
    }

    /**
     * Get business rule configuration
     */
    public BusinessRuleConfiguration getConfiguration() {
        return new BusinessRuleConfiguration(
            allowNegativeBalance,
            maxTransactionAmount,
            minTransactionAmount,
            maxDailyTransactionLimit,
            requireFutureDateValidation
        );
    }

    /**
     * Inner class for business rule configuration
     */
    public static class BusinessRuleConfiguration {
        private final boolean allowNegativeBalance;
        private final BigDecimal maxTransactionAmount;
        private final BigDecimal minTransactionAmount;
        private final BigDecimal maxDailyTransactionLimit;
        private final boolean requireFutureDateValidation;

        public BusinessRuleConfiguration(boolean allowNegativeBalance, BigDecimal maxTransactionAmount, 
                                       BigDecimal minTransactionAmount, BigDecimal maxDailyTransactionLimit, 
                                       boolean requireFutureDateValidation) {
            this.allowNegativeBalance = allowNegativeBalance;
            this.maxTransactionAmount = maxTransactionAmount;
            this.minTransactionAmount = minTransactionAmount;
            this.maxDailyTransactionLimit = maxDailyTransactionLimit;
            this.requireFutureDateValidation = requireFutureDateValidation;
        }

        // Getters
        public boolean isAllowNegativeBalance() { return allowNegativeBalance; }
        public BigDecimal getMaxTransactionAmount() { return maxTransactionAmount; }
        public BigDecimal getMinTransactionAmount() { return minTransactionAmount; }
        public BigDecimal getMaxDailyTransactionLimit() { return maxDailyTransactionLimit; }
        public boolean isRequireFutureDateValidation() { return requireFutureDateValidation; }
    }
}
