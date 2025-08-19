package com.example.ledgerly.controller;

import com.example.ledgerly.dto.BasicUserResponse;
import com.example.ledgerly.dto.UserCreateRequest;
import com.example.ledgerly.dto.UserUpdateRequest;
import com.example.ledgerly.entity.Role;
import com.example.ledgerly.entity.Shop;
import com.example.ledgerly.entity.StaffShopMapping;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.repository.ShopRepository;
import com.example.ledgerly.repository.StaffShopMappingRepository;
import com.example.ledgerly.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1/owner/staff")
public class OwnerController {

    private static final Logger logger = LoggerFactory.getLogger(OwnerController.class);
    
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final StaffShopMappingRepository staffShopMappingRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public OwnerController(UserRepository userRepository, 
                         ShopRepository shopRepository,
                         StaffShopMappingRepository staffShopMappingRepository,
                         PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
        this.staffShopMappingRepository = staffShopMappingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    @PostMapping
    public ResponseEntity<BasicUserResponse> createStaff(@Valid @RequestBody UserCreateRequest request, Authentication auth) {
        logger.debug("Creating staff with request: {}", request);
        logger.debug("Current authentication: {}", auth);
        logger.debug("Current authorities: {}", auth != null ? auth.getAuthorities() : "null");
        
        if (request.getRole() != Role.STAFF) {
            logger.warn("Invalid role requested: {}, expected STAFF", request.getRole());
            return ResponseEntity.badRequest().build();
        }
        if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Username or email already exists: username={}, email={}", request.getUsername(), request.getEmail());
            return ResponseEntity.status(409).build();
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.STAFF);
        user.setEmailVerified(true);
        user.setActive(true);
        User saved = userRepository.save(user);
        logger.info("Successfully created staff user: {}", saved.getUsername());
        return ResponseEntity.created(URI.create("/api/v1/owner/staff/" + saved.getId())).body(toDto(saved));
    }

    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BasicUserResponse> updateStaff(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return userRepository.findById(id)
                .filter(u -> u.getRole() == Role.STAFF)
                .map(u -> {
                    u.setFirstName(request.getFirstName());
                    u.setLastName(request.getLastName());
                    if (request.getEmail() != null) u.setEmail(request.getEmail());
                    if (request.getPhoneNumber() != null) u.setPhoneNumber(request.getPhoneNumber());
                    return ResponseEntity.ok(toDto(userRepository.save(u)));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable Long id) {
        return userRepository.findById(id)
                .filter(u -> u.getRole() == Role.STAFF)
                .map(u -> {
                    userRepository.delete(u);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    @GetMapping
    public ResponseEntity<List<BasicUserResponse>> listStaff() {
        List<BasicUserResponse> staff = userRepository.findByRole(Role.STAFF).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(staff);
    }
    
    /**
     * Assign staff to a specific shop
     */
    @PostMapping("/{staffId}/assign-shop/{shopId}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Map<String, Object>> assignStaffToShop(@PathVariable Long staffId, 
                                                               @PathVariable Long shopId,
                                                               Authentication authentication) {
        try {
            // Get current user
            User currentUser = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get staff user
            User staff = userRepository.findById(staffId)
                    .orElseThrow(() -> new RuntimeException("Staff user not found"));
            
            if (staff.getRole() != Role.STAFF) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "User is not a staff member");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Get shop
            Shop shop = shopRepository.findById(shopId)
                    .orElseThrow(() -> new RuntimeException("Shop not found"));
            
            // Check if current user owns this shop or is admin
            if (!shop.getOwner().getId().equals(currentUser.getId()) && 
                !currentUser.getRole().name().equals("ADMIN")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Access denied: You don't own this shop");
                return ResponseEntity.status(403).body(errorResponse);
            }
            
            // Check if staff is already assigned to any shop
            if (staffShopMappingRepository.existsByStaffId(staffId)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Staff is already assigned to a shop");
                return ResponseEntity.status(409).body(errorResponse);
            }
            
            // Create staff-shop mapping
            StaffShopMapping mapping = new StaffShopMapping(staff, shop);
            staffShopMappingRepository.save(mapping);
            
            logger.info("Successfully assigned staff {} to shop {}", staff.getUsername(), shop.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Staff assigned to shop successfully");
            response.put("staffId", staffId);
            response.put("shopId", shopId);
            response.put("shopName", shop.getName());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error assigning staff to shop: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to assign staff to shop: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    /**
     * Remove staff from shop
     */
    @DeleteMapping("/{staffId}/remove-shop")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Map<String, Object>> removeStaffFromShop(@PathVariable Long staffId,
                                                                 Authentication authentication) {
        try {
            // Get current user
            User currentUser = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Get staff-shop mapping
            StaffShopMapping mapping = staffShopMappingRepository.findByStaffId(staffId)
                    .orElseThrow(() -> new RuntimeException("Staff is not assigned to any shop"));
            
            // Check if current user owns this shop or is admin
            if (!mapping.getShop().getOwner().getId().equals(currentUser.getId()) && 
                !currentUser.getRole().name().equals("ADMIN")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Access denied: You don't own this shop");
                return ResponseEntity.status(403).body(errorResponse);
            }
            
            // Remove mapping
            staffShopMappingRepository.delete(mapping);
            
            logger.info("Successfully removed staff {} from shop {}", 
                       mapping.getStaff().getUsername(), mapping.getShop().getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Staff removed from shop successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error removing staff from shop: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to remove staff from shop: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

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


