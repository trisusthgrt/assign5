package com.example.ledgerly.service;

import com.example.ledgerly.entity.AuditLog;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.repository.AuditLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for managing audit logs
 */
@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuditService(AuditLogRepository auditLogRepository, ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Log a successful operation
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logSuccess(String action, String entityType, Long entityId, 
                          Object oldValues, Object newValues, String description, User user) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setAction(action);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setDescription(description);
            auditLog.setUser(user);
            auditLog.setSuccess(true);

            if (oldValues != null) {
                auditLog.setOldValues(convertToJson(oldValues));
            }
            if (newValues != null) {
                auditLog.setNewValues(convertToJson(newValues));
            }

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            // Don't fail the main operation if audit logging fails
            System.err.println("Failed to log audit: " + e.getMessage());
        }
    }

    /**
     * Log a failed operation
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logFailure(String action, String entityType, Long entityId, 
                          String errorMessage, String description, User user) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setAction(action);
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setDescription(description);
            auditLog.setErrorMessage(errorMessage);
            auditLog.setUser(user);
            auditLog.setSuccess(false);

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            // Don't fail the main operation if audit logging fails
            System.err.println("Failed to log audit failure: " + e.getMessage());
        }
    }

    /**
     * Log business rule violation
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logBusinessRuleViolation(String action, String ruleCode, String ruleDescription, 
                                       String entityType, Long entityId, User user) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setAction("BUSINESS_RULE_VIOLATION");
            auditLog.setEntityType(entityType);
            auditLog.setEntityId(entityId);
            auditLog.setDescription(String.format("Rule: %s - %s. Original Action: %s", ruleCode, ruleDescription, action));
            auditLog.setErrorMessage(ruleDescription);
            auditLog.setUser(user);
            auditLog.setSuccess(false);

            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            System.err.println("Failed to log business rule violation: " + e.getMessage());
        }
    }

    /**
     * Log simple action
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(String action, String description, User user) {
        logSuccess(action, null, null, null, null, description, user);
    }

    /**
     * Get audit logs by user with pagination
     */
    public Page<AuditLog> getAuditLogsByUser(Long userId, Pageable pageable) {
        return auditLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * Get audit logs for entity
     */
    public List<AuditLog> getAuditLogsForEntity(String entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId);
    }

    /**
     * Search audit logs
     */
    public Page<AuditLog> searchAuditLogs(Long userId, String action, String entityType, 
                                        Long entityId, Boolean success, 
                                        LocalDateTime startDate, LocalDateTime endDate, 
                                        Pageable pageable) {
        return auditLogRepository.searchAuditLogs(userId, action, entityType, entityId, 
                                                success, startDate, endDate, pageable);
    }

    /**
     * Get failed operations
     */
    public Page<AuditLog> getFailedOperations(Pageable pageable) {
        return auditLogRepository.findBySuccessFalseOrderByCreatedAtDesc(pageable);
    }

    /**
     * Get recent audit logs
     */
    public List<AuditLog> getRecentLogs(int hours) {
        LocalDateTime since = LocalDateTime.now().minusHours(hours);
        return auditLogRepository.findRecentLogs(since);
    }

    /**
     * Get audit statistics
     */
    public AuditStatistics getAuditStatistics() {
        long totalLogs = auditLogRepository.count();
        long successfulLogs = auditLogRepository.countBySuccess(true);
        long failedLogs = auditLogRepository.countBySuccess(false);
        
        List<Object[]> actionStats = auditLogRepository.getAuditStatsByAction();
        List<Object[]> userStats = auditLogRepository.getAuditStatsByUser();
        
        return new AuditStatistics(totalLogs, successfulLogs, failedLogs, actionStats, userStats);
    }

    /**
     * Convert object to JSON string
     */
    private String convertToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "Error serializing object: " + e.getMessage();
        }
    }

    /**
     * Create audit snapshot of an object
     */
    public Map<String, Object> createAuditSnapshot(Object entity) {
        Map<String, Object> snapshot = new HashMap<>();
        try {
            String json = objectMapper.writeValueAsString(entity);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            return map;
        } catch (Exception e) {
            snapshot.put("error", "Failed to create snapshot: " + e.getMessage());
            snapshot.put("entity", entity.toString());
            return snapshot;
        }
    }

    /**
     * Inner class for audit statistics
     */
    public static class AuditStatistics {
        private final long totalLogs;
        private final long successfulLogs;
        private final long failedLogs;
        private final List<Object[]> actionStatistics;
        private final List<Object[]> userStatistics;

        public AuditStatistics(long totalLogs, long successfulLogs, long failedLogs, 
                             List<Object[]> actionStatistics, List<Object[]> userStatistics) {
            this.totalLogs = totalLogs;
            this.successfulLogs = successfulLogs;
            this.failedLogs = failedLogs;
            this.actionStatistics = actionStatistics;
            this.userStatistics = userStatistics;
        }

        public long getTotalLogs() { return totalLogs; }
        public long getSuccessfulLogs() { return successfulLogs; }
        public long getFailedLogs() { return failedLogs; }
        public List<Object[]> getActionStatistics() { return actionStatistics; }
        public List<Object[]> getUserStatistics() { return userStatistics; }
        
        public double getSuccessRate() {
            if (totalLogs == 0) return 0.0;
            return (double) successfulLogs / totalLogs * 100;
        }
        
        public double getFailureRate() {
            if (totalLogs == 0) return 0.0;
            return (double) failedLogs / totalLogs * 100;
        }
    }
}
