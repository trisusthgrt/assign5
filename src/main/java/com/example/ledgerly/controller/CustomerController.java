package com.example.ledgerly.controller;

import com.example.ledgerly.dto.CustomerCreateRequest;
import com.example.ledgerly.dto.CustomerResponse;
import com.example.ledgerly.dto.CustomerUpdateRequest;
import com.example.ledgerly.entity.RelationshipType;
import com.example.ledgerly.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import java.util.stream.Collectors;
import java.math.BigDecimal;

/**
 * REST Controller for Customer management operations
 */
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Create a new customer
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> createCustomer(@Valid @RequestBody CustomerCreateRequest request,
                                                             Authentication authentication) {
        try {
            String username = authentication.getName();
            CustomerResponse customer = customerService.createCustomer(request, username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Customer created successfully");
            response.put("customer", customer);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Create customer in specific shop (for owners)
     */
    @PostMapping("/shop/{shopId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> createCustomerInShop(@Valid @RequestBody CustomerCreateRequest request,
                                                                   @PathVariable Long shopId,
                                                                   Authentication authentication) {
        try {
            String username = authentication.getName();
            CustomerResponse customer = customerService.createCustomerInShop(request, username, shopId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Customer created successfully in shop");
            response.put("customer", customer);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get customer by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> getCustomerById(@PathVariable Long id, Authentication authentication) {
        try {
            String username = authentication.getName();
            CustomerResponse customer = customerService.getCustomerById(id, username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customer", customer);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Update an existing customer
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> updateCustomer(@PathVariable Long id,
                                                             @Valid @RequestBody CustomerUpdateRequest request,
                                                             Authentication authentication) {
        try {
            String username = authentication.getName();
            CustomerResponse customer = customerService.updateCustomer(id, request, username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Customer updated successfully");
            response.put("customer", customer);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Delete a customer (soft delete)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> deleteCustomer(@PathVariable Long id, Authentication authentication) {
        try {
            String username = authentication.getName();
            customerService.deleteCustomer(id, username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Customer deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get all customers with pagination
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> getAllCustomers(Authentication authentication) {
        try {
            String username = authentication.getName();
            List<CustomerResponse> customers = customerService.getAllCustomers(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customers", customers);
            response.put("count", customers.size());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get customers by shop ID
     */
    @GetMapping("/shop/{shopId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> getCustomersByShop(@PathVariable Long shopId, 
                                                                 Authentication authentication) {
        try {
            String username = authentication.getName();
            List<CustomerResponse> customers = customerService.getCustomersByShop(shopId, username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customers", customers);
            response.put("count", customers.size());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get active customers
     */
    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> getActiveCustomers(Authentication authentication) {
        try {
            String username = authentication.getName();
            // Get all customers (they are already filtered by active status in the service)
            List<CustomerResponse> customers = customerService.getAllCustomers(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customers", customers);
            response.put("count", customers.size());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Search customers by name
     */
    @GetMapping("/search/name")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> searchCustomersByName(@RequestParam String name,
                                                                   Authentication authentication) {
        try {
            String username = authentication.getName();
            List<CustomerResponse> customers = customerService.searchCustomers(name, username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customers", customers);
            response.put("count", customers.size());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Search customers by multiple criteria
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> searchCustomers(@RequestParam String searchTerm, 
                                                              Authentication authentication) {
        try {
            String username = authentication.getName();
            List<CustomerResponse> customers = customerService.searchCustomers(searchTerm, username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customers", customers);
            response.put("count", customers.size());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get customers by relationship type
     */
    @GetMapping("/relationship/{relationshipType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> getCustomersByRelationshipType(@PathVariable RelationshipType relationshipType,
                                                                           Authentication authentication) {
        try {
            String username = authentication.getName();
            // Filter customers by relationship type from the user's accessible shops
            List<CustomerResponse> allCustomers = customerService.getAllCustomers(username);
            List<CustomerResponse> filteredCustomers = allCustomers.stream()
                    .filter(customer -> customer.getRelationshipType() == relationshipType)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customers", filteredCustomers);
            response.put("count", filteredCustomers.size());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get customers with outstanding balance
     */
    @GetMapping("/outstanding-balance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> getCustomersWithOutstandingBalance(Authentication authentication) {
        try {
            String username = authentication.getName();
            // Get all customers and filter by outstanding balance
            List<CustomerResponse> allCustomers = customerService.getAllCustomers(username);
            List<CustomerResponse> outstandingCustomers = allCustomers.stream()
                    .filter(customer -> customer.getCurrentBalance().compareTo(BigDecimal.ZERO) > 0)
                    .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("customers", outstandingCustomers);
            response.put("count", outstandingCustomers.size());
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get customer statistics
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> getCustomerStatistics(Authentication authentication) {
        try {
            String username = authentication.getName();
            CustomerService.CustomerStatistics stats = customerService.getCustomerStatistics(username);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("statistics", stats);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get all relationship types
     */
    @GetMapping("/relationship-types")
    public ResponseEntity<Map<String, Object>> getRelationshipTypes() {
        try {
            RelationshipType[] types = RelationshipType.values();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("relationshipTypes", types);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch relationship types: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
