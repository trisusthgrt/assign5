package com.example.ledgerly.repository;

import com.example.ledgerly.entity.PaymentApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for PaymentApplication entity operations
 */
@Repository
public interface PaymentApplicationRepository extends JpaRepository<PaymentApplication, Long> {

    /**
     * Find applications by payment ID
     */
    List<PaymentApplication> findByPaymentIdAndIsReversedFalseOrderByAppliedAtDesc(Long paymentId);

    /**
     * Find applications by ledger entry ID
     */
    List<PaymentApplication> findByLedgerEntryIdAndIsReversedFalseOrderByAppliedAtDesc(Long ledgerEntryId);

    /**
     * Find applications by customer
     */
    @Query("SELECT pa FROM PaymentApplication pa WHERE pa.payment.customer.id = :customerId AND pa.isReversed = false ORDER BY pa.appliedAt DESC")
    List<PaymentApplication> findByCustomerId(@Param("customerId") Long customerId);

    /**
     * Find applications by customer with pagination
     */
    @Query("SELECT pa FROM PaymentApplication pa WHERE pa.payment.customer.id = :customerId AND pa.isReversed = false ORDER BY pa.appliedAt DESC")
    Page<PaymentApplication> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

    /**
     * Find reversed applications
     */
    List<PaymentApplication> findByIsReversedTrueOrderByReversedAtDesc();

    /**
     * Find applications by date range
     */
    @Query("SELECT pa FROM PaymentApplication pa WHERE pa.appliedAt BETWEEN :startDate AND :endDate AND pa.isReversed = false ORDER BY pa.appliedAt DESC")
    List<PaymentApplication> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Calculate total applied amount for a payment
     */
    @Query("SELECT COALESCE(SUM(pa.appliedAmount), 0) FROM PaymentApplication pa WHERE pa.payment.id = :paymentId AND pa.isReversed = false")
    BigDecimal calculateTotalAppliedForPayment(@Param("paymentId") Long paymentId);

    /**
     * Calculate total applied amount for a ledger entry
     */
    @Query("SELECT COALESCE(SUM(pa.appliedAmount), 0) FROM PaymentApplication pa WHERE pa.ledgerEntry.id = :ledgerEntryId AND pa.isReversed = false")
    BigDecimal calculateTotalAppliedForLedgerEntry(@Param("ledgerEntryId") Long ledgerEntryId);

    /**
     * Find applications by user
     */
    List<PaymentApplication> findByAppliedByIdAndIsReversedFalseOrderByAppliedAtDesc(Long userId);

    /**
     * Find applications that can be reversed (not already reversed)
     */
    @Query("SELECT pa FROM PaymentApplication pa WHERE pa.payment.id = :paymentId AND pa.isReversed = false ORDER BY pa.appliedAt DESC")
    List<PaymentApplication> findReversibleApplicationsByPayment(@Param("paymentId") Long paymentId);

    /**
     * Find recent applications
     */
    @Query("SELECT pa FROM PaymentApplication pa WHERE pa.appliedAt >= :since AND pa.isReversed = false ORDER BY pa.appliedAt DESC")
    List<PaymentApplication> findRecentApplications(@Param("since") LocalDateTime since);

    /**
     * Count total applications
     */
    long countByIsReversedFalse();

    /**
     * Count applications by payment
     */
    long countByPaymentIdAndIsReversedFalse(Long paymentId);

    /**
     * Count applications by ledger entry
     */
    long countByLedgerEntryIdAndIsReversedFalse(Long ledgerEntryId);

    /**
     * Search applications by multiple criteria
     */
    @Query("SELECT pa FROM PaymentApplication pa WHERE " +
           "(:paymentId IS NULL OR pa.payment.id = :paymentId) AND " +
           "(:ledgerEntryId IS NULL OR pa.ledgerEntry.id = :ledgerEntryId) AND " +
           "(:customerId IS NULL OR pa.payment.customer.id = :customerId) AND " +
           "(:userId IS NULL OR pa.appliedBy.id = :userId) AND " +
           "(:minAmount IS NULL OR pa.appliedAmount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR pa.appliedAmount <= :maxAmount) AND " +
           "(:startDate IS NULL OR pa.appliedAt >= :startDate) AND " +
           "(:endDate IS NULL OR pa.appliedAt <= :endDate) AND " +
           "(:isReversed IS NULL OR pa.isReversed = :isReversed) " +
           "ORDER BY pa.appliedAt DESC")
    Page<PaymentApplication> searchApplications(@Param("paymentId") Long paymentId,
                                              @Param("ledgerEntryId") Long ledgerEntryId,
                                              @Param("customerId") Long customerId,
                                              @Param("userId") Long userId,
                                              @Param("minAmount") BigDecimal minAmount,
                                              @Param("maxAmount") BigDecimal maxAmount,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate,
                                              @Param("isReversed") Boolean isReversed,
                                              Pageable pageable);

    /**
     * Get application statistics by user
     */
    @Query("SELECT pa.appliedBy.username, COUNT(pa), SUM(pa.appliedAmount) FROM PaymentApplication pa WHERE pa.isReversed = false GROUP BY pa.appliedBy.username ORDER BY COUNT(pa) DESC")
    List<Object[]> getApplicationStatsByUser();

    /**
     * Get monthly application summary
     */
    @Query("SELECT YEAR(pa.appliedAt), MONTH(pa.appliedAt), COUNT(pa), SUM(pa.appliedAmount) FROM PaymentApplication pa WHERE pa.isReversed = false GROUP BY YEAR(pa.appliedAt), MONTH(pa.appliedAt) ORDER BY YEAR(pa.appliedAt) DESC, MONTH(pa.appliedAt) DESC")
    List<Object[]> getMonthlySummary();

    /**
     * Find applications for specific payment and ledger entry combination
     */
    @Query("SELECT pa FROM PaymentApplication pa WHERE pa.payment.id = :paymentId AND pa.ledgerEntry.id = :ledgerEntryId AND pa.isReversed = false")
    List<PaymentApplication> findByPaymentAndLedgerEntry(@Param("paymentId") Long paymentId, @Param("ledgerEntryId") Long ledgerEntryId);
}
