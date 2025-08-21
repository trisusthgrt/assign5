package com.example.ledgerly.controller;

import com.example.ledgerly.dto.AuthResponse;
import com.example.ledgerly.dto.ChangePasswordRequest;
import com.example.ledgerly.dto.LoginRequest;
import com.example.ledgerly.dto.RegisterRequest;
import com.example.ledgerly.dto.RoleAssignmentRequest;
import com.example.ledgerly.entity.Role;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication and user management endpoints")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(
        summary = "Register a new user",
        description = "Register a new user with the specified role. Only STAFF role can self-register."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data or user already exists")
    })
    public ResponseEntity<AuthResponse> register(
            @Parameter(description = "User registration details", required = true)
            @Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = userService.register(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    @Operation(
        summary = "User login",
        description = "Authenticate user and return JWT token"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<AuthResponse> login(
            @Parameter(description = "Login credentials", required = true)
            @Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get current user profile",
        description = "Retrieve the profile of the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("firstName", user.getFirstName());
        userInfo.put("lastName", user.getLastName());
        userInfo.put("role", user.getRole());
        userInfo.put("fullName", user.getFirstName() + " " + user.getLastName());
        userInfo.put("emailVerified", user.isEmailVerified());
        userInfo.put("phoneVerified", user.isPhoneVerified());
        userInfo.put("createdAt", user.getCreatedAt());
        userInfo.put("lastLogin", user.getLastLogin());

        return ResponseEntity.ok(userInfo);
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @Operation(
        summary = "Get all active users",
        description = "Retrieve list of all active users. Requires ADMIN or OWNER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findActiveUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get users by role",
        description = "Retrieve list of users with a specific role. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<User>> getUsersByRole(
            @Parameter(description = "Role to filter users by", required = true)
            @PathVariable Role role) {
        List<User> users = userService.findByRole(role);
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @Operation(
        summary = "Deactivate user",
        description = "Deactivate a user account. Requires ADMIN or OWNER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deactivated successfully"),
        @ApiResponse(responseCode = "400", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, String>> deactivateUser(
            @Parameter(description = "ID of the user to deactivate", required = true)
            @PathVariable Long userId) {
        try {
            userService.deactivateUser(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deactivated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/users/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @Operation(
        summary = "Activate user",
        description = "Activate a previously deactivated user account. Requires ADMIN or OWNER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User activated successfully"),
        @ApiResponse(responseCode = "400", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, String>> activateUser(
            @Parameter(description = "ID of the user to activate", required = true)
            @PathVariable Long userId) {
        try {
            userService.activateUser(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User activated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/users/{userId}/verify-email")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Verify user email",
        description = "Mark a user's email as verified. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email verified successfully"),
        @ApiResponse(responseCode = "400", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, String>> verifyEmail(
            @Parameter(description = "ID of the user whose email to verify", required = true)
            @PathVariable Long userId) {
        try {
            userService.verifyEmail(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Email verified successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "Get user statistics",
        description = "Retrieve user count statistics. Requires ADMIN role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.countActiveUsers());
        stats.put("ownerCount", userService.countByRole(Role.OWNER));
        stats.put("staffCount", userService.countByRole(Role.STAFF));
        stats.put("adminCount", userService.countByRole(Role.ADMIN));
        
        return ResponseEntity.ok(stats);
    }

    @PutMapping("/users/assign-role")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    @Operation(
        summary = "Assign role to user",
        description = "Assign a new role to a user. Requires ADMIN or OWNER role."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role assigned successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or user not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, Object>> assignRole(
            @Parameter(description = "Role assignment request", required = true)
            @Valid @RequestBody RoleAssignmentRequest request) {
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

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("firstName", user.getFirstName());
            userInfo.put("lastName", user.getLastName());
            userInfo.put("role", user.getRole());
            userInfo.put("fullName", user.getFirstName() + " " + user.getLastName());
            userInfo.put("emailVerified", user.isEmailVerified());
            userInfo.put("phoneVerified", user.isPhoneVerified());
            userInfo.put("createdAt", user.getCreatedAt());
            userInfo.put("lastLogin", user.getLastLogin());

            return ResponseEntity.ok(userInfo);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> authHealthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Authentication Service");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test-auth")
    public ResponseEntity<Map<String, Object>> testAuth(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        if (authentication != null && authentication.isAuthenticated()) {
            response.put("authenticated", true);
            response.put("username", authentication.getName());
            response.put("authorities", authentication.getAuthorities().stream()
                    .map(Object::toString)
                    .collect(Collectors.toList()));
            response.put("principal", authentication.getPrincipal().getClass().getSimpleName());
        } else {
            response.put("authenticated", false);
            response.put("message", "No authentication found");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    @Operation(
        summary = "Change user password",
        description = "Change password for a user. Requires username/email and current password verification."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password changed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<Map<String, Object>> changePassword(
            @Parameter(description = "Password change request", required = true)
            @Valid @RequestBody ChangePasswordRequest request) {
        
        logger.info("Change password request received for user: {}", request.getUsernameOrEmail());
        
        try {
            boolean success = userService.changePassword(
                request.getUsernameOrEmail(), 
                request.getCurrentPassword(), 
                request.getNewPassword()
            );
            
            logger.info("Password changed successfully for user: {}", request.getUsernameOrEmail());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", success);
            response.put("message", "Password changed successfully");
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            logger.warn("Password change failed for user {}: {}", request.getUsernameOrEmail(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
