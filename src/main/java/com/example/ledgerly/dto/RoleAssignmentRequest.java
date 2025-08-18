package com.example.ledgerly.dto;

import com.example.ledgerly.entity.Role;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for role assignment
 */
public class RoleAssignmentRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Role is required")
    private Role role;

    // Constructors
    public RoleAssignmentRequest() {
    }

    public RoleAssignmentRequest(Long userId, Role role) {
        this.userId = userId;
        this.role = role;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
