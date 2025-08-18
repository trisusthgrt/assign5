package com.example.ledgerly.controller;

import com.example.ledgerly.entity.AuditLog;
import com.example.ledgerly.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Audit Log management
 */
@RestController
@RequestMapping("/api/v1/audit")
@PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
public class AuditController {

    private final AuditService auditService;

    @Autowired
    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    /**
     * Search audit logs with filters
     */
    @GetMapping("/logs")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> searchAuditLogs(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) Long entityId,
            @RequestParam(required = false) Boolean success,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<AuditLog> auditLogs = auditService.searchAuditLogs(
                    userId, action, entityType, entityId, success, startDate, endDate, pageable);
            
            List<Map<String, Object>> logs = auditLogs.getContent().stream()
                    .map(this::convertAuditLogToMap)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("logs", logs);
            response.put("totalElements", auditLogs.getTotalElements());
            response.put("totalPages", auditLogs.getTotalPages());
            response.put("currentPage", auditLogs.getNumber());
            response.put("size", auditLogs.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to search audit logs: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Get audit logs for a specific entity
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> getAuditLogsForEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        
        try {
            List<AuditLog> auditLogs = auditService.getAuditLogsForEntity(entityType, entityId);
            
            List<Map<String, Object>> logs = auditLogs.stream()
                    .map(this::convertAuditLogToMap)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("logs", logs);
            response.put("entityType", entityType);
            response.put("entityId", entityId);
            response.put("count", logs.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch audit logs for entity: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Get failed operations
     */
    @GetMapping("/failures")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getFailedOperations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<AuditLog> failedLogs = auditService.getFailedOperations(pageable);
            
            List<Map<String, Object>> logs = failedLogs.getContent().stream()
                    .map(this::convertAuditLogToMap)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("failedOperations", logs);
            response.put("totalElements", failedLogs.getTotalElements());
            response.put("totalPages", failedLogs.getTotalPages());
            response.put("currentPage", failedLogs.getNumber());
            response.put("size", failedLogs.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch failed operations: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Get recent audit logs
     */
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> getRecentLogs(
            @RequestParam(defaultValue = "24") int hours) {
        
        try {
            List<AuditLog> recentLogs = auditService.getRecentLogs(hours);
            
            List<Map<String, Object>> logs = recentLogs.stream()
                    .limit(50) // Limit to 50 recent logs
                    .map(this::convertAuditLogToMap)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("recentLogs", logs);
            response.put("hours", hours);
            response.put("count", logs.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch recent logs: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Get audit statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAuditStatistics() {
        try {
            AuditService.AuditStatistics stats = auditService.getAuditStatistics();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("statistics", Map.of(
                    "totalLogs", stats.getTotalLogs(),
                    "successfulLogs", stats.getSuccessfulLogs(),
                    "failedLogs", stats.getFailedLogs(),
                    "successRate", String.format("%.2f%%", stats.getSuccessRate()),
                    "failureRate", String.format("%.2f%%", stats.getFailureRate()),
                    "actionStatistics", convertActionStatistics(stats.getActionStatistics()),
                    "userStatistics", convertUserStatistics(stats.getUserStatistics())
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch audit statistics: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Get audit logs by user
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('OWNER') and #userId == authentication.principal.id)")
    public ResponseEntity<Map<String, Object>> getAuditLogsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<AuditLog> userLogs = auditService.getAuditLogsByUser(userId, pageable);
            
            List<Map<String, Object>> logs = userLogs.getContent().stream()
                    .map(this::convertAuditLogToMap)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("logs", logs);
            response.put("userId", userId);
            response.put("totalElements", userLogs.getTotalElements());
            response.put("totalPages", userLogs.getTotalPages());
            response.put("currentPage", userLogs.getNumber());
            response.put("size", userLogs.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch user audit logs: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Convert AuditLog to Map for JSON response
     */
    private Map<String, Object> convertAuditLogToMap(AuditLog auditLog) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", auditLog.getId());
        map.put("action", auditLog.getAction());
        map.put("entityType", auditLog.getEntityType());
        map.put("entityId", auditLog.getEntityId());
        map.put("description", auditLog.getDescription());
        map.put("success", auditLog.isSuccess());
        map.put("errorMessage", auditLog.getErrorMessage());
        map.put("createdAt", auditLog.getCreatedAt());
        map.put("username", auditLog.getUser() != null ? auditLog.getUser().getUsername() : null);
        map.put("ipAddress", auditLog.getIpAddress());
        map.put("userAgent", auditLog.getUserAgent());
        
        // Only include values for authorized users
        if (auditLog.getOldValues() != null && auditLog.getOldValues().length() < 1000) {
            map.put("oldValues", auditLog.getOldValues());
        }
        if (auditLog.getNewValues() != null && auditLog.getNewValues().length() < 1000) {
            map.put("newValues", auditLog.getNewValues());
        }
        
        return map;
    }

    /**
     * Convert action statistics to readable format
     */
    private List<Map<String, Object>> convertActionStatistics(List<Object[]> actionStats) {
        return actionStats.stream()
                .limit(10) // Top 10 actions
                .map(stat -> Map.of(
                        "action", stat[0],
                        "totalCount", stat[1],
                        "successCount", stat[2],
                        "failureCount", stat[3]
                ))
                .collect(Collectors.toList());
    }

    /**
     * Convert user statistics to readable format
     */
    private List<Map<String, Object>> convertUserStatistics(List<Object[]> userStats) {
        return userStats.stream()
                .limit(10) // Top 10 users
                .map(stat -> Map.of(
                        "username", stat[0],
                        "totalCount", stat[1],
                        "successCount", stat[2],
                        "failureCount", stat[3]
                ))
                .collect(Collectors.toList());
    }
}
