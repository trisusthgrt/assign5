package com.example.ledgerly.repository;

import com.example.ledgerly.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for AuditLog entity operations
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Find audit logs by user ID
     */
    List<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find audit logs by user ID with pagination
     */
    Page<AuditLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    /**
     * Find audit logs by entity type and ID
     */
    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(String entityType, Long entityId);

    /**
     * Find audit logs by action
     */
    List<AuditLog> findByActionOrderByCreatedAtDesc(String action);

    /**
     * Find audit logs by action with pagination
     */
    Page<AuditLog> findByActionOrderByCreatedAtDesc(String action, Pageable pageable);

    /**
     * Find audit logs by success status
     */
    List<AuditLog> findBySuccessOrderByCreatedAtDesc(boolean success);

    /**
     * Find failed operations
     */
    Page<AuditLog> findBySuccessFalseOrderByCreatedAtDesc(Pageable pageable);

    /**
     * Find audit logs by date range
     */
    @Query("SELECT al FROM AuditLog al WHERE al.createdAt BETWEEN :startDate AND :endDate ORDER BY al.createdAt DESC")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find audit logs by date range with pagination
     */
    @Query("SELECT al FROM AuditLog al WHERE al.createdAt BETWEEN :startDate AND :endDate ORDER BY al.createdAt DESC")
    Page<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    /**
     * Search audit logs by multiple criteria
     */
    @Query("SELECT al FROM AuditLog al WHERE " +
           "(:userId IS NULL OR al.user.id = :userId) AND " +
           "(:action IS NULL OR LOWER(al.action) LIKE LOWER(CONCAT('%', :action, '%'))) AND " +
           "(:entityType IS NULL OR LOWER(al.entityType) LIKE LOWER(CONCAT('%', :entityType, '%'))) AND " +
           "(:entityId IS NULL OR al.entityId = :entityId) AND " +
           "(:success IS NULL OR al.success = :success) AND " +
           "(:startDate IS NULL OR al.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR al.createdAt <= :endDate) " +
           "ORDER BY al.createdAt DESC")
    Page<AuditLog> searchAuditLogs(@Param("userId") Long userId,
                                  @Param("action") String action,
                                  @Param("entityType") String entityType,
                                  @Param("entityId") Long entityId,
                                  @Param("success") Boolean success,
                                  @Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate,
                                  Pageable pageable);

    /**
     * Count total audit logs
     */
    long count();

    /**
     * Count audit logs by success status
     */
    long countBySuccess(boolean success);

    /**
     * Count audit logs by user
     */
    long countByUserId(Long userId);

    /**
     * Count audit logs by action
     */
    long countByAction(String action);

    /**
     * Find recent audit logs (last N hours)
     */
    @Query("SELECT al FROM AuditLog al WHERE al.createdAt >= :since ORDER BY al.createdAt DESC")
    List<AuditLog> findRecentLogs(@Param("since") LocalDateTime since);

    /**
     * Get audit statistics by action
     */
    @Query("SELECT al.action, COUNT(al), SUM(CASE WHEN al.success = true THEN 1 ELSE 0 END), SUM(CASE WHEN al.success = false THEN 1 ELSE 0 END) FROM AuditLog al GROUP BY al.action ORDER BY COUNT(al) DESC")
    List<Object[]> getAuditStatsByAction();

    /**
     * Get audit statistics by user
     */
    @Query("SELECT al.user.username, COUNT(al), SUM(CASE WHEN al.success = true THEN 1 ELSE 0 END), SUM(CASE WHEN al.success = false THEN 1 ELSE 0 END) FROM AuditLog al GROUP BY al.user.username ORDER BY COUNT(al) DESC")
    List<Object[]> getAuditStatsByUser();
}
