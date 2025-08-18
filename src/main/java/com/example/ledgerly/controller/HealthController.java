package com.example.ledgerly.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check controller for monitoring application status
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    /**
     * Basic health check endpoint
     * @return ResponseEntity with health status information
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Ledgerly application is running successfully");
        response.put("timestamp", LocalDateTime.now());
        response.put("application", "Ledgerly");
        response.put("version", "0.0.1-SNAPSHOT");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Detailed health check endpoint
     * @return ResponseEntity with detailed health information
     */
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealthCheck() {
        Map<String, Object> response = new HashMap<>();
        
        // Basic application info
        response.put("status", "UP");
        response.put("application", "Ledgerly - Small Business Ledger & Finance Manager");
        response.put("version", "0.0.1-SNAPSHOT");
        response.put("timestamp", LocalDateTime.now());
        
        // System information
        Map<String, Object> system = new HashMap<>();
        system.put("java_version", System.getProperty("java.version"));
        system.put("java_vendor", System.getProperty("java.vendor"));
        system.put("os_name", System.getProperty("os.name"));
        system.put("os_version", System.getProperty("os.version"));
        system.put("available_processors", Runtime.getRuntime().availableProcessors());
        system.put("max_memory_mb", Runtime.getRuntime().maxMemory() / 1024 / 1024);
        system.put("total_memory_mb", Runtime.getRuntime().totalMemory() / 1024 / 1024);
        system.put("free_memory_mb", Runtime.getRuntime().freeMemory() / 1024 / 1024);
        response.put("system", system);
        
        // Application components status (placeholder for future components)
        Map<String, String> components = new HashMap<>();
        components.put("web_server", "UP");
        components.put("application_context", "UP");
        // Future: database, cache, external services, etc.
        response.put("components", components);
        
        return ResponseEntity.ok(response);
    }
}
