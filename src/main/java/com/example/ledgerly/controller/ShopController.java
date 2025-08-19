package com.example.ledgerly.controller;

import com.example.ledgerly.dto.ShopCreateRequest;
import com.example.ledgerly.dto.ShopResponse;
import com.example.ledgerly.entity.Shop;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.repository.ShopRepository;
import com.example.ledgerly.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for Shop management operations
 */
@RestController
@RequestMapping("/api/v1/shops")
public class ShopController {

    private static final Logger logger = LoggerFactory.getLogger(ShopController.class);
    
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    @Autowired
    public ShopController(ShopRepository shopRepository, UserRepository userRepository) {
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new shop (OWNER only)
     */
    @PostMapping
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createShop(@Valid @RequestBody ShopCreateRequest request, 
                                                         Authentication authentication) {
        try {
            logger.debug("Creating shop with request: {}", request);
            
            // Get current user
            User currentUser = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            // Check if shop name already exists for this owner
            if (shopRepository.existsByNameAndOwnerId(request.getName(), currentUser.getId())) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Shop name already exists for this owner");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }
            
            // Create shop
            Shop shop = new Shop();
            shop.setName(request.getName());
            shop.setDescription(request.getDescription());
            shop.setAddress(request.getAddress());
            shop.setPhoneNumber(request.getPhoneNumber());
            shop.setEmail(request.getEmail());
            shop.setGstNumber(request.getGstNumber());
            shop.setPanNumber(request.getPanNumber());
            shop.setCity(request.getCity());
            shop.setState(request.getState());
            shop.setPincode(request.getPincode());
            shop.setOwner(currentUser);
            
            Shop savedShop = shopRepository.save(shop);
            logger.info("Successfully created shop: {} for owner: {}", savedShop.getName(), currentUser.getUsername());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Shop created successfully");
            response.put("shop", new ShopResponse(savedShop));
            
            return ResponseEntity.created(URI.create("/api/v1/shops/" + savedShop.getId())).body(response);
            
        } catch (Exception e) {
            logger.error("Error creating shop: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to create shop: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Get all shops owned by current user
     */
    @GetMapping("/my-shops")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getMyShops(Authentication authentication) {
        try {
            User currentUser = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            List<Shop> shops = shopRepository.findByOwnerIdAndIsActiveTrue(currentUser.getId());
            List<ShopResponse> shopResponses = shops.stream()
                    .map(ShopResponse::new)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("shops", shopResponses);
            response.put("count", shopResponses.size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching shops: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch shops: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get shop by ID (OWNER of shop or ADMIN)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getShopById(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Shop shop = shopRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Shop not found"));
            
            // Check if user owns this shop or is admin
            if (!shop.getOwner().getId().equals(currentUser.getId()) && 
                !currentUser.getRole().name().equals("ADMIN")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Access denied: You don't own this shop");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("shop", new ShopResponse(shop));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching shop: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch shop: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Update shop (OWNER of shop or ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateShop(@PathVariable Long id, 
                                                         @Valid @RequestBody ShopCreateRequest request,
                                                         Authentication authentication) {
        try {
            User currentUser = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Shop shop = shopRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Shop not found"));
            
            // Check if user owns this shop or is admin
            if (!shop.getOwner().getId().equals(currentUser.getId()) && 
                !currentUser.getRole().name().equals("ADMIN")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Access denied: You don't own this shop");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
            
            // Update shop fields
            shop.setName(request.getName());
            shop.setDescription(request.getDescription());
            shop.setAddress(request.getAddress());
            shop.setPhoneNumber(request.getPhoneNumber());
            shop.setEmail(request.getEmail());
            shop.setGstNumber(request.getGstNumber());
            shop.setPanNumber(request.getPanNumber());
            shop.setCity(request.getCity());
            shop.setState(request.getState());
            shop.setPincode(request.getPincode());
            
            Shop updatedShop = shopRepository.save(shop);
            logger.info("Successfully updated shop: {}", updatedShop.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Shop updated successfully");
            response.put("shop", new ShopResponse(updatedShop));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error updating shop: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update shop: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Delete shop (OWNER of shop or ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteShop(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            Shop shop = shopRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Shop not found"));
            
            // Check if user owns this shop or is admin
            if (!shop.getOwner().getId().equals(currentUser.getId()) && 
                !currentUser.getRole().name().equals("ADMIN")) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Access denied: You don't own this shop");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
            }
            
            // Soft delete - set as inactive
            shop.setActive(false);
            shopRepository.save(shop);
            logger.info("Successfully deactivated shop: {}", shop.getName());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Shop deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error deleting shop: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to delete shop: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
