package com.example.ledgerly.controller;

import com.example.ledgerly.dto.ProfileUpdateRequest;
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
import java.util.Map;

/**
 * REST Controller for user profile management
 */
@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {

    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get current user's profile
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentUserProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User user = userService.getUserProfile(username);
            
            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("username", user.getUsername());
            profile.put("email", user.getEmail());
            profile.put("firstName", user.getFirstName());
            profile.put("lastName", user.getLastName());
            profile.put("fullName", user.getFullName());
            profile.put("phoneNumber", user.getPhoneNumber());
            profile.put("role", user.getRole());
            profile.put("businessName", user.getBusinessName());
            profile.put("businessAddress", user.getBusinessAddress());
            profile.put("isActive", user.isActive());
            profile.put("isEmailVerified", user.isEmailVerified());
            profile.put("isPhoneVerified", user.isPhoneVerified());
            profile.put("createdAt", user.getCreatedAt());
            profile.put("updatedAt", user.getUpdatedAt());
            profile.put("lastLogin", user.getLastLogin());
            
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Update current user's profile
     */
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateCurrentUserProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User currentUser = userService.getUserProfile(username);
            
            User updatedUser = userService.updateProfile(
                currentUser.getId(),
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getBusinessName(),
                request.getBusinessAddress()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Profile updated successfully");
            response.put("profile", createProfileResponse(updatedUser));
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
     * Update user profile by ID (Admin and Owner only)
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> updateUserProfile(@PathVariable Long userId, 
                                                                @Valid @RequestBody ProfileUpdateRequest request) {
        try {
            User updatedUser = userService.updateProfile(
                userId,
                request.getFirstName(),
                request.getLastName(),
                request.getEmail(),
                request.getPhoneNumber(),
                request.getBusinessName(),
                request.getBusinessAddress()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User profile updated successfully");
            response.put("profile", createProfileResponse(updatedUser));
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
     * Get profile by user ID (Admin and Owner only)
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Map<String, Object> profile = createProfileResponse(user);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Update business details only
     */
    @PutMapping("/business")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateBusinessDetails(@RequestBody Map<String, String> businessDetails) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User currentUser = userService.getUserProfile(username);
            
            String businessName = businessDetails.get("businessName");
            String businessAddress = businessDetails.get("businessAddress");
            
            User updatedUser = userService.updateProfile(
                currentUser.getId(),
                currentUser.getFirstName(),
                currentUser.getLastName(),
                currentUser.getEmail(),
                currentUser.getPhoneNumber(),
                businessName,
                businessAddress
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Business details updated successfully");
            response.put("businessName", updatedUser.getBusinessName());
            response.put("businessAddress", updatedUser.getBusinessAddress());
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
     * Update contact info only
     */
    @PutMapping("/contact")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateContactInfo(@RequestBody Map<String, String> contactInfo) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            User currentUser = userService.getUserProfile(username);
            
            String email = contactInfo.getOrDefault("email", currentUser.getEmail());
            String phoneNumber = contactInfo.get("phoneNumber");
            
            User updatedUser = userService.updateProfile(
                currentUser.getId(),
                currentUser.getFirstName(),
                currentUser.getLastName(),
                email,
                phoneNumber,
                currentUser.getBusinessName(),
                currentUser.getBusinessAddress()
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Contact information updated successfully");
            response.put("email", updatedUser.getEmail());
            response.put("phoneNumber", updatedUser.getPhoneNumber());
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
     * Helper method to create profile response
     */
    private Map<String, Object> createProfileResponse(User user) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("username", user.getUsername());
        profile.put("email", user.getEmail());
        profile.put("firstName", user.getFirstName());
        profile.put("lastName", user.getLastName());
        profile.put("fullName", user.getFullName());
        profile.put("phoneNumber", user.getPhoneNumber());
        profile.put("role", user.getRole());
        profile.put("businessName", user.getBusinessName());
        profile.put("businessAddress", user.getBusinessAddress());
        profile.put("isActive", user.isActive());
        profile.put("isEmailVerified", user.isEmailVerified());
        profile.put("isPhoneVerified", user.isPhoneVerified());
        profile.put("createdAt", user.getCreatedAt());
        profile.put("updatedAt", user.getUpdatedAt());
        profile.put("lastLogin", user.getLastLogin());
        return profile;
    }
}
