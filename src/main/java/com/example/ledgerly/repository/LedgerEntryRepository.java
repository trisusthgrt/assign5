package com.example.ledgerly.repository;

import com.example.ledgerly.entity.Customer;
import com.example.ledgerly.entity.LedgerEntry;
import com.example.ledgerly.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for LedgerEntry entity operations
 */
@Repository
public interface LedgerEntryRepository extends JpaRepository<LedgerEntry, Long> {

    /**
     * Find ledger entries by customer
     */
    List<LedgerEntry> findByCustomerAndIsActiveTrueOrderByTransactionDateDesc(Customer customer);

    /**
     * Find ledger entries by customer with pagination
     */
    Page<LedgerEntry> findByCustomerAndIsActiveTrueOrderByTransactionDateDesc(Customer customer, Pageable pageable);

    /**
     * Find ledger entries by customer ID
     */
    List<LedgerEntry> findByCustomerIdAndIsActiveTrueOrderByTransactionDateDesc(Long customerId);

    /**
     * Find ledger entries by customer ID with pagination
     */
    Page<LedgerEntry> findByCustomerIdAndIsActiveTrueOrderByTransactionDateDesc(Long customerId, Pageable pageable);

    /**
     * Find ledger entries by transaction type
     */
    List<LedgerEntry> findByTransactionTypeAndIsActiveTrueOrderByTransactionDateDesc(TransactionType transactionType);

    /**
     * Find ledger entries by date range
     */
    @Query("SELECT le FROM LedgerEntry le WHERE le.transactionDate BETWEEN :startDate AND :endDate AND le.isActive = true ORDER BY le.transactionDate DESC")
    List<LedgerEntry> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find ledger entries by date range with pagination
     */
    @Query("SELECT le FROM LedgerEntry le WHERE le.transactionDate BETWEEN :startDate AND :endDate AND le.isActive = true ORDER BY le.transactionDate DESC")
    Page<LedgerEntry> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    /**
     * Find ledger entries by customer and date range
     */
    @Query("SELECT le FROM LedgerEntry le WHERE le.customer.id = :customerId AND le.transactionDate BETWEEN :startDate AND :endDate AND le.isActive = true ORDER BY le.transactionDate DESC")
    List<LedgerEntry> findByCustomerAndDateRange(@Param("customerId") Long customerId, 
                                                @Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate);

    /**
     * Find ledger entries by customer and date range with pagination
     */
    @Query("SELECT le FROM LedgerEntry le WHERE le.customer.id = :customerId AND le.transactionDate BETWEEN :startDate AND :endDate AND le.isActive = true ORDER BY le.transactionDate DESC")
    Page<LedgerEntry> findByCustomerAndDateRange(@Param("customerId") Long customerId, 
                                                @Param("startDate") LocalDate startDate, 
                                                @Param("endDate") LocalDate endDate, Pageable pageable);

    /**
     * Search ledger entries by multiple criteria
     */
    @Query("SELECT le FROM LedgerEntry le WHERE " +
           "(:customerId IS NULL OR le.customer.id = :customerId) AND " +
           "(:transactionType IS NULL OR le.transactionType = :transactionType) AND " +
           "(:startDate IS NULL OR le.transactionDate >= :startDate) AND " +
           "(:endDate IS NULL OR le.transactionDate <= :endDate) AND " +
           "(:minAmount IS NULL OR le.amount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR le.amount <= :maxAmount) AND " +
           "(:description IS NULL OR LOWER(le.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
           "(:referenceNumber IS NULL OR LOWER(le.referenceNumber) LIKE LOWER(CONCAT('%', :referenceNumber, '%'))) AND " +
           "(:invoiceNumber IS NULL OR LOWER(le.invoiceNumber) LIKE LOWER(CONCAT('%', :invoiceNumber, '%'))) AND " +
           "(:isReconciled IS NULL OR le.isReconciled = :isReconciled) AND " +
           "le.isActive = true " +
           "ORDER BY le.transactionDate DESC")
    Page<LedgerEntry> searchLedgerEntries(@Param("customerId") Long customerId,
                                        @Param("transactionType") TransactionType transactionType,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate,
                                        @Param("minAmount") BigDecimal minAmount,
                                        @Param("maxAmount") BigDecimal maxAmount,
                                        @Param("description") String description,
                                        @Param("referenceNumber") String referenceNumber,
                                        @Param("invoiceNumber") String invoiceNumber,
                                        @Param("isReconciled") Boolean isReconciled,
                                        Pageable pageable);

    /**
     * Calculate total credit amount for a customer
     */
    @Query("SELECT COALESCE(SUM(le.amount), 0) FROM LedgerEntry le WHERE le.customer.id = :customerId AND le.transactionType IN ('CREDIT', 'OPENING_BALANCE') AND le.isActive = true")
    BigDecimal calculateTotalCreditForCustomer(@Param("customerId") Long customerId);

    /**
     * Calculate total debit amount for a customer
     */
    @Query("SELECT COALESCE(SUM(le.amount), 0) FROM LedgerEntry le WHERE le.customer.id = :customerId AND le.transactionType = 'DEBIT' AND le.isActive = true")
    BigDecimal calculateTotalDebitForCustomer(@Param("customerId") Long customerId);

    /**
     * Calculate current balance for a customer
     */
    @Query("SELECT COALESCE(SUM(CASE WHEN le.transactionType IN ('CREDIT', 'OPENING_BALANCE') THEN le.amount ELSE -le.amount END), 0) FROM LedgerEntry le WHERE le.customer.id = :customerId AND le.isActive = true")
    BigDecimal calculateCurrentBalanceForCustomer(@Param("customerId") Long customerId);

    /**
     * Find latest ledger entry for a customer
     */
    @Query("SELECT le FROM LedgerEntry le WHERE le.customer.id = :customerId AND le.isActive = true ORDER BY le.transactionDate DESC, le.createdAt DESC")
    List<LedgerEntry> findLatestEntryForCustomer(@Param("customerId") Long customerId, Pageable pageable);

    /**
     * Find unreconciled entries
     */
    List<LedgerEntry> findByIsReconciledFalseAndIsActiveTrueOrderByTransactionDateDesc();

    /**
     * Find unreconciled entries for a customer
     */
    List<LedgerEntry> findByCustomerIdAndIsReconciledFalseAndIsActiveTrueOrderByTransactionDateDesc(Long customerId);

    /**
     * Count total entries
     */
    long countByIsActiveTrue();

    /**
     * Count entries by transaction type
     */
    long countByTransactionTypeAndIsActiveTrue(TransactionType transactionType);

    /**
     * Find entries with specific reference number
     */
    List<LedgerEntry> findByReferenceNumberIgnoreCaseAndIsActiveTrue(String referenceNumber);

    /**
     * Find entries with specific invoice number
     */
    List<LedgerEntry> findByInvoiceNumberIgnoreCaseAndIsActiveTrue(String invoiceNumber);

    /**
     * Find entries by payment method
     */
    List<LedgerEntry> findByPaymentMethodIgnoreCaseAndIsActiveTrueOrderByTransactionDateDesc(String paymentMethod);

    /**
     * Find recent entries (last N days)
     */
    @Query("SELECT le FROM LedgerEntry le WHERE le.transactionDate >= :startDate AND le.isActive = true ORDER BY le.transactionDate DESC")
    List<LedgerEntry> findRecentEntries(@Param("startDate") LocalDate startDate);

    /**
     * Get monthly summary for a customer
     */
    @Query("SELECT le.transactionType, SUM(le.amount), COUNT(le) FROM LedgerEntry le WHERE le.customer.id = :customerId AND YEAR(le.transactionDate) = :year AND MONTH(le.transactionDate) = :month AND le.isActive = true GROUP BY le.transactionType")
    List<Object[]> getMonthlySummaryForCustomer(@Param("customerId") Long customerId, @Param("year") int year, @Param("month") int month);

    /**
     * Get yearly summary for a customer
     */
    @Query("SELECT le.transactionType, SUM(le.amount), COUNT(le) FROM LedgerEntry le WHERE le.customer.id = :customerId AND YEAR(le.transactionDate) = :year AND le.isActive = true GROUP BY le.transactionType")
    List<Object[]> getYearlySummaryForCustomer(@Param("customerId") Long customerId, @Param("year") int year);
}
