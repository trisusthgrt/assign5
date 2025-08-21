package com.example.ledgerly.repository;

import com.example.ledgerly.entity.Customer;
import com.example.ledgerly.entity.Payment;
import com.example.ledgerly.entity.PaymentStatus;
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
 * Repository interface for Payment entity operations
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payments by customer
     */
    List<Payment> findByCustomerAndIsActiveTrueOrderByPaymentDateDesc(Customer customer);

    /**
     * Find payments by customer with pagination
     */
    Page<Payment> findByCustomerAndIsActiveTrueOrderByPaymentDateDesc(Customer customer, Pageable pageable);

    /**
     * Find payments by customer ID
     */
    List<Payment> findByCustomerIdAndIsActiveTrueOrderByPaymentDateDesc(Long customerId);

    /**
     * Find payments by customer ID with pagination
     */
    Page<Payment> findByCustomerIdAndIsActiveTrueOrderByPaymentDateDesc(Long customerId, Pageable pageable);

    /**
     * Find payments by status
     */
    List<Payment> findByStatusAndIsActiveTrueOrderByPaymentDateDesc(PaymentStatus status);

    /**
     * Find payments by status with pagination
     */
    Page<Payment> findByStatusAndIsActiveTrueOrderByPaymentDateDesc(PaymentStatus status, Pageable pageable);

    /**
     * Find unapplied payments (partial or pending)
     */
    @Query("SELECT p FROM Payment p WHERE p.status IN ('PENDING', 'PARTIAL') AND p.isActive = true ORDER BY p.paymentDate ASC")
    List<Payment> findUnappliedPayments();

    /**
     * Find unapplied payments for a customer
     */
    @Query("SELECT p FROM Payment p WHERE p.customer.id = :customerId AND p.status IN ('PENDING', 'PARTIAL') AND p.isActive = true ORDER BY p.paymentDate ASC")
    List<Payment> findUnappliedPaymentsByCustomer(@Param("customerId") Long customerId);

    /**
     * Find unapplied payments for a customer with pagination
     */
    @Query("SELECT p FROM Payment p WHERE p.customer.id = :customerId AND p.status IN ('PENDING', 'PARTIAL') AND p.isActive = true ORDER BY p.paymentDate ASC")
    Page<Payment> findUnappliedPaymentsByCustomer(@Param("customerId") Long customerId, Pageable pageable);

    /**
     * Find payments by date range
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate AND p.isActive = true ORDER BY p.paymentDate DESC")
    List<Payment> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find payments by date range with pagination
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate AND p.isActive = true ORDER BY p.paymentDate DESC")
    Page<Payment> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, Pageable pageable);

    /**
     * Search payments by multiple criteria
     */
    @Query("SELECT p FROM Payment p WHERE " +
           "(:customerId IS NULL OR p.customer.id = :customerId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:startDate IS NULL OR p.paymentDate >= :startDate) AND " +
           "(:endDate IS NULL OR p.paymentDate <= :endDate) AND " +
           "(:minAmount IS NULL OR p.amount >= :minAmount) AND " +
           "(:maxAmount IS NULL OR p.amount <= :maxAmount) AND " +
           "(:description IS NULL OR LOWER(p.description) LIKE LOWER(CONCAT('%', :description, '%'))) AND " +
           "(:referenceNumber IS NULL OR LOWER(p.referenceNumber) LIKE LOWER(CONCAT('%', :referenceNumber, '%'))) AND " +
           "(:paymentMethod IS NULL OR LOWER(p.paymentMethod) LIKE LOWER(CONCAT('%', :paymentMethod, '%'))) AND " +
           "(:isAdvancePayment IS NULL OR p.isAdvancePayment = :isAdvancePayment) AND " +
           "p.isActive = true " +
           "ORDER BY p.paymentDate DESC")
    Page<Payment> searchPayments(@Param("customerId") Long customerId,
                                @Param("status") PaymentStatus status,
                                @Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate,
                                @Param("minAmount") BigDecimal minAmount,
                                @Param("maxAmount") BigDecimal maxAmount,
                                @Param("description") String description,
                                @Param("referenceNumber") String referenceNumber,
                                @Param("paymentMethod") String paymentMethod,
                                @Param("isAdvancePayment") Boolean isAdvancePayment,
                                Pageable pageable);

    /**
     * Calculate total payments for a customer
     */
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.customer.id = :customerId AND p.isActive = true")
    BigDecimal calculateTotalPaymentsForCustomer(@Param("customerId") Long customerId);

    /**
     * Calculate total applied payments for a customer
     */
    @Query("SELECT COALESCE(SUM(p.appliedAmount), 0) FROM Payment p WHERE p.customer.id = :customerId AND p.isActive = true")
    BigDecimal calculateTotalAppliedPaymentsForCustomer(@Param("customerId") Long customerId);

    /**
     * Calculate total unapplied payments for a customer
     */
    @Query("SELECT COALESCE(SUM(p.amount - p.appliedAmount), 0) FROM Payment p WHERE p.customer.id = :customerId AND p.status IN ('PENDING', 'PARTIAL') AND p.isActive = true")
    BigDecimal calculateTotalUnappliedPaymentsForCustomer(@Param("customerId") Long customerId);

    /**
     * Find advance payments for a customer
     */
    List<Payment> findByCustomerIdAndIsAdvancePaymentTrueAndIsActiveTrueOrderByPaymentDateDesc(Long customerId);

    /**
     * Find payments by reference number
     */
    List<Payment> findByReferenceNumberIgnoreCaseAndIsActiveTrue(String referenceNumber);

    /**
     * Find payments by check number
     */
    List<Payment> findByCheckNumberIgnoreCaseAndIsActiveTrue(String checkNumber);

    /**
     * Find payments by payment method
     */
    List<Payment> findByPaymentMethodIgnoreCaseAndIsActiveTrueOrderByPaymentDateDesc(String paymentMethod);

    /**
     * Count total payments
     */
    long countByIsActiveTrue();

    /**
     * Count payments by status
     */
    long countByStatusAndIsActiveTrue(PaymentStatus status);

    /**
     * Count payments by customer
     */
    long countByCustomerIdAndIsActiveTrue(Long customerId);

    /**
     * Find recent payments (last N days)
     */
    @Query("SELECT p FROM Payment p WHERE p.paymentDate >= :startDate AND p.isActive = true ORDER BY p.paymentDate DESC")
    List<Payment> findRecentPayments(@Param("startDate") LocalDate startDate);

    /**
     * Get monthly payment summary for a customer
     */
    @Query("SELECT p.status, SUM(p.amount), COUNT(p) FROM Payment p WHERE p.customer.id = :customerId AND YEAR(p.paymentDate) = :year AND MONTH(p.paymentDate) = :month AND p.isActive = true GROUP BY p.status")
    List<Object[]> getMonthlySummaryForCustomer(@Param("customerId") Long customerId, @Param("year") int year, @Param("month") int month);

    /**
     * Get yearly payment summary for a customer
     */
    @Query("SELECT p.status, SUM(p.amount), COUNT(p) FROM Payment p WHERE p.customer.id = :customerId AND YEAR(p.paymentDate) = :year AND p.isActive = true GROUP BY p.status")
    List<Object[]> getYearlySummaryForCustomer(@Param("customerId") Long customerId, @Param("year") int year);

    /**
     * Find payments with remaining balance
     */
    @Query("SELECT p FROM Payment p WHERE p.remainingAmount > 0 AND p.isActive = true ORDER BY p.paymentDate ASC")
    List<Payment> findPaymentsWithRemainingBalance();

    /**
     * Find payments that can be applied (have unapplied amount)
     */
    @Query("SELECT p FROM Payment p WHERE p.amount > p.appliedAmount AND p.status IN ('PENDING', 'PARTIAL') AND p.isActive = true ORDER BY p.paymentDate ASC")
    List<Payment> findApplicablePayments();

    /**
     * Find applicable payments for a customer
     */
    @Query("SELECT p FROM Payment p WHERE p.customer.id = :customerId AND p.amount > p.appliedAmount AND p.status IN ('PENDING', 'PARTIAL') AND p.isActive = true ORDER BY p.paymentDate ASC")
    List<Payment> findApplicablePaymentsByCustomer(@Param("customerId") Long customerId);

    /**
     * Find payments by shop IDs with pagination
     */
    Page<Payment> findByShopIdInAndIsActiveTrueOrderByPaymentDateDesc(List<Long> shopIds, Pageable pageable);
}
