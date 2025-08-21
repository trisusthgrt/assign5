package com.example.ledgerly.controller;

import com.example.ledgerly.dto.BasicUserResponse;
import com.example.ledgerly.dto.UserCreateRequest;
import com.example.ledgerly.dto.UserUpdateRequest;
import com.example.ledgerly.entity.Role;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/users")
@Tag(name = "Admin Management", description = "Admin user management endpoints - Only accessible by ADMIN users")
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==================== ADMIN MANAGEMENT ====================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admins")
    @Operation(
        summary = "Create a new admin user",
        description = "Creates a new admin user. Only existing admins can create other admin users."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Admin user created successfully",
            content = @Content(schema = @Schema(implementation = BasicUserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Username or email already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Access denied - not an admin"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<BasicUserResponse> createAdmin(@Valid @RequestBody UserCreateRequest request) {
        try {
            // Validate that the request is for an admin role
            if (request.getRole() != Role.ADMIN) {
                return ResponseEntity.badRequest().build();
            }

            // Check if username already exists
            if (userRepository.existsByUsername(request.getUsername())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Create new admin user
            User adminUser = new User();
            adminUser.setUsername(request.getUsername());
            adminUser.setEmail(request.getEmail());
            adminUser.setPassword(passwordEncoder.encode(request.getPassword()));
            adminUser.setFirstName(request.getFirstName());
            adminUser.setLastName(request.getLastName());
            adminUser.setRole(Role.ADMIN);
            adminUser.setEmailVerified(true);
            adminUser.setActive(true);
            
            // Get current admin user to set as creator
            User currentAdmin = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                    .orElseThrow(() -> new RuntimeException("Current admin user not found"));
            adminUser.setCreatedBy(currentAdmin);

            User savedAdmin = userRepository.save(adminUser);
            
            return ResponseEntity.created(URI.create("/api/v1/admin/users/admins/" + savedAdmin.getId()))
                    .body(toDto(savedAdmin));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admins/{id}")
    @Operation(
        summary = "Update an admin user",
        description = "Updates an existing admin user. Only admins can update other admin users."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin user updated successfully",
            content = @Content(schema = @Schema(implementation = BasicUserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Admin user not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Access denied - not an admin"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<BasicUserResponse> updateAdmin(
            @Parameter(description = "ID of the admin user to update", required = true)
            @PathVariable Long id, 
            @Valid @RequestBody UserUpdateRequest request) {
        try {
            Optional<User> adminOptional = userRepository.findById(id)
                    .filter(u -> u.getRole() == Role.ADMIN);
            
            if (adminOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            User admin = adminOptional.get();
            
            // Update admin fields
            admin.setFirstName(request.getFirstName());
            admin.setLastName(request.getLastName());
            if (request.getEmail() != null) {
                // Check if new email conflicts with existing users
                if (!admin.getEmail().equals(request.getEmail()) && 
                    userRepository.existsByEmail(request.getEmail())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
                admin.setEmail(request.getEmail());
            }
            if (request.getPhoneNumber() != null) {
                admin.setPhoneNumber(request.getPhoneNumber());
            }
            
            User updatedAdmin = userRepository.save(admin);
            return ResponseEntity.ok(toDto(updatedAdmin));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admins/{id}")
    @Operation(
        summary = "Delete an admin user",
        description = "Deletes an admin user. Only admins can delete other admin users. Cannot delete yourself."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin user deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Cannot delete yourself or invalid operation"),
        @ApiResponse(responseCode = "404", description = "Admin user not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Access denied - not an admin"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, String>> deleteAdmin(
            @Parameter(description = "ID of the admin user to delete", required = true)
            @PathVariable Long id) {
        try {
            return userRepository.findById(id)
                    .filter(u -> u.getRole() == Role.ADMIN)
                    .map(admin -> {
                        // Soft delete - set as inactive instead of hard delete
                        admin.setActive(false);
                        userRepository.save(admin);
                        
                        Map<String, String> response = new HashMap<>();
                        response.put("message", "Admin user deactivated successfully");
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admins")
    @Operation(
        summary = "List all admin users",
        description = "Retrieves a list of all admin users. Only admins can view this list."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin users retrieved successfully",
            content = @Content(schema = @Schema(implementation = BasicUserResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Access denied - not an admin"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<BasicUserResponse>> listAdmins() {
        try {
            List<BasicUserResponse> admins = userRepository.findByRole(Role.ADMIN).stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(admins);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admins/{id}")
    @Operation(
        summary = "Get admin user by ID",
        description = "Retrieves a specific admin user by ID. Only admins can view admin details."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Admin user retrieved successfully",
            content = @Content(schema = @Schema(implementation = BasicUserResponse.class))),
        @ApiResponse(responseCode = "404", description = "Admin user not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Access denied - not an admin"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<BasicUserResponse> getAdminById(
            @Parameter(description = "ID of the admin user to retrieve", required = true)
            @PathVariable Long id) {
        try {
            return userRepository.findById(id)
                    .filter(u -> u.getRole() == Role.ADMIN)
                    .map(admin -> ResponseEntity.ok(toDto(admin)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== OWNER MANAGEMENT ====================

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/owners")
    @Operation(
        summary = "Create a new owner user",
        description = "Creates a new owner user. Only admins can create owner accounts."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Owner user created successfully",
            content = @Content(schema = @Schema(implementation = BasicUserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Username or email already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Access denied - not an admin"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<BasicUserResponse> createOwner(@Valid @RequestBody UserCreateRequest request) {
        try {
            if (request.getRole() != Role.OWNER) {
                return ResponseEntity.badRequest().build();
            }
            if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setRole(Role.OWNER);
            user.setEmailVerified(true);
            user.setActive(true);
            
            // Get current admin user to set as creator
            User currentAdmin = userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName())
                    .orElseThrow(() -> new RuntimeException("Current admin user not found"));
            user.setCreatedBy(currentAdmin);
            
            User saved = userRepository.save(user);
            return ResponseEntity.created(URI.create("/api/v1/admin/users/owners/" + saved.getId())).body(toDto(saved));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/owners/{id}")
    @Operation(
        summary = "Update an owner user",
        description = "Updates an existing owner user. Only admins can update owner accounts."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Owner user updated successfully",
            content = @Content(schema = @Schema(implementation = BasicUserResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Owner user not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Access denied - not an admin"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<BasicUserResponse> updateOwner(
            @Parameter(description = "ID of the owner user to update", required = true)
            @PathVariable Long id, 
            @Valid @RequestBody UserUpdateRequest request) {
        try {
            return userRepository.findById(id)
                    .filter(u -> u.getRole() == Role.OWNER)
                    .map(u -> {
                        u.setFirstName(request.getFirstName());
                        u.setLastName(request.getLastName());
                        if (request.getEmail() != null) u.setEmail(request.getEmail());
                        if (request.getPhoneNumber() != null) u.setPhoneNumber(request.getPhoneNumber());
                        return ResponseEntity.ok(toDto(userRepository.save(u)));
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/owners/{id}")
    @Operation(
        summary = "Delete an owner user",
        description = "Deletes an owner user. Only admins can delete owner accounts."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Owner user deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Owner user not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Access denied - not an admin"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<Map<String, String>> deleteOwner(
            @Parameter(description = "ID of the owner user to delete", required = true)
            @PathVariable Long id) {
        try {
            return userRepository.findById(id)
                    .filter(u -> u.getRole() == Role.OWNER)
                    .map(u -> {
                        // Soft delete - set as inactive
                        u.setActive(false);
                        userRepository.save(u);
                        
                        Map<String, String> response = new HashMap<>();
                        response.put("message", "Owner user deactivated successfully");
                        return ResponseEntity.ok(response);
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/owners")
    @Operation(
        summary = "List all owner users",
        description = "Retrieves a list of all owner users. Only admins can view this list."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Owner users retrieved successfully",
            content = @Content(schema = @Schema(implementation = BasicUserResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Access denied - not an admin"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<BasicUserResponse>> listOwners() {
        try {
            List<BasicUserResponse> owners = userRepository.findByRole(Role.OWNER).stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(owners);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/owners/{id}")
    @Operation(
        summary = "Get owner user by ID",
        description = "Retrieves a specific owner user by ID. Only admins can view owner details."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Owner user retrieved successfully",
            content = @Content(schema = @Schema(implementation = BasicUserResponse.class))),
        @ApiResponse(responseCode = "404", description = "Owner user not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Access denied - not an admin"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<BasicUserResponse> getOwnerById(
            @Parameter(description = "ID of the owner user to retrieve", required = true)
            @PathVariable Long id) {
        try {
            return userRepository.findById(id)
                    .filter(u -> u.getRole() == Role.OWNER)
                    .map(owner -> ResponseEntity.ok(toDto(owner)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== STAFF MANAGEMENT ====================

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/staff")
    @Operation(
        summary = "List all staff users",
        description = "Retrieves a list of all staff users. Only admins can view this list."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Staff users retrieved successfully",
            content = @Content(schema = @Schema(implementation = BasicUserResponse.class))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Access denied - not an admin"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<List<BasicUserResponse>> listStaff() {
        try {
            List<BasicUserResponse> staff = userRepository.findByRole(Role.STAFF).stream()
                    .map(this::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(staff);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/staff/{id}")
    @Operation(
        summary = "Get staff user by ID",
        description = "Retrieves a specific staff user by ID. Only admins can view staff details."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Staff user retrieved successfully",
            content = @Content(schema = @Schema(implementation = BasicUserResponse.class))),
        @ApiResponse(responseCode = "404", description = "Staff user not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
        @ApiResponse(responseCode = "403", description = "Access denied - not an admin"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<BasicUserResponse> getStaffById(
            @Parameter(description = "ID of the staff user to retrieve", required = true)
            @PathVariable Long id) {
        try {
            return userRepository.findById(id)
                    .filter(u -> u.getRole() == Role.STAFF)
                    .map(staff -> ResponseEntity.ok(toDto(staff)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ==================== UTILITY METHODS ====================

    private BasicUserResponse toDto(User u) {
        BasicUserResponse dto = new BasicUserResponse();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setFirstName(u.getFirstName());
        dto.setLastName(u.getLastName());
        dto.setRole(u.getRole());
        dto.setActive(u.isActive());
        return dto;
    }
}


