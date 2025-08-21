package com.example.ledgerly.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
@Tag(name = "Health Check", description = "Application health monitoring endpoints")
public class HealthController {

    @GetMapping
    @Operation(
        summary = "Basic health check",
        description = "Check if the Ledgerly application is running"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Application is healthy and running")
    })
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Ledgerly application is running successfully");
        response.put("timestamp", LocalDateTime.now());
        response.put("application", "Ledgerly");
        response.put("version", "0.0.1-SNAPSHOT");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detailed")
    @Operation(
        summary = "Detailed health check",
        description = "Get comprehensive health information including system details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Detailed health information retrieved")
    })
    public ResponseEntity<Map<String, Object>> detailedHealthCheck() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("status", "UP");
        response.put("application", "Ledgerly - Small Business Ledger & Finance Manager");
        response.put("version", "0.0.1-SNAPSHOT");
        response.put("timestamp", LocalDateTime.now());
        
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
        
        Map<String, String> components = new HashMap<>();
        components.put("web_server", "UP");
        components.put("application_context", "UP");
        response.put("components", components);
        
        return ResponseEntity.ok(response);
    }
}
