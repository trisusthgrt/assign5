package com.example.ledgerly.controller;

import com.example.ledgerly.dto.AuthResponse;
import com.example.ledgerly.dto.LoginRequest;
import com.example.ledgerly.dto.RegisterRequest;
import com.example.ledgerly.dto.RoleAssignmentRequest;
import com.example.ledgerly.entity.Role;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for authentication operations
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new user
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = userService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Login user
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * Get current user profile
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("firstName", user.getFirstName());
        userInfo.put("lastName", user.getLastName());
        userInfo.put("fullName", user.getFullName());
        userInfo.put("role", user.getRole());
        userInfo.put("businessName", user.getBusinessName());
        userInfo.put("businessAddress", user.getBusinessAddress());
        userInfo.put("phoneNumber", user.getPhoneNumber());
        userInfo.put("isActive", user.isActive());
        userInfo.put("isEmailVerified", user.isEmailVerified());
        userInfo.put("isPhoneVerified", user.isPhoneVerified());
        userInfo.put("createdAt", user.getCreatedAt());
        userInfo.put("lastLogin", user.getLastLogin());

        return ResponseEntity.ok(userInfo);
    }

    /**
     * Get all users (Admin and Owner only)
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findActiveUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get users by role (Admin only)
     */
    @GetMapping("/users/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        List<User> users = userService.findByRole(role);
        return ResponseEntity.ok(users);
    }

    /**
     * Deactivate user (Admin and Owner only)
     */
    @PutMapping("/users/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, String>> deactivateUser(@PathVariable Long userId) {
        try {
            userService.deactivateUser(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deactivated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Activate user (Admin and Owner only)
     */
    @PutMapping("/users/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, String>> activateUser(@PathVariable Long userId) {
        try {
            userService.activateUser(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User activated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Verify email (Admin only)
     */
    @PutMapping("/users/{userId}/verify-email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> verifyEmail(@PathVariable Long userId) {
        try {
            userService.verifyEmail(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email verified successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get user statistics (Admin only)
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.countActiveUsers());
        stats.put("ownerCount", userService.countByRole(Role.OWNER));
        stats.put("staffCount", userService.countByRole(Role.STAFF));
        stats.put("adminCount", userService.countByRole(Role.ADMIN));
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Assign role to user (Admin and Owner only)
     */
    @PutMapping("/users/assign-role")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> assignRole(@Valid @RequestBody RoleAssignmentRequest request) {
        try {
            User updatedUser = userService.assignRole(request.getUserId(), request.getRole());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Role assigned successfully");
            response.put("userId", updatedUser.getId());
            response.put("username", updatedUser.getUsername());
            response.put("newRole", updatedUser.getRole());
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get user by ID (Admin and Owner only)
     */
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("email", user.getEmail());
            userInfo.put("firstName", user.getFirstName());
            userInfo.put("lastName", user.getLastName());
            userInfo.put("fullName", user.getFullName());
            userInfo.put("role", user.getRole());
            userInfo.put("businessName", user.getBusinessName());
            userInfo.put("businessAddress", user.getBusinessAddress());
            userInfo.put("phoneNumber", user.getPhoneNumber());
            userInfo.put("isActive", user.isActive());
            userInfo.put("isEmailVerified", user.isEmailVerified());
            userInfo.put("isPhoneVerified", user.isPhoneVerified());
            userInfo.put("createdAt", user.getCreatedAt());
            userInfo.put("lastLogin", user.getLastLogin());

            return ResponseEntity.ok(userInfo);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Health check for auth service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> authHealthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Authentication Service");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
