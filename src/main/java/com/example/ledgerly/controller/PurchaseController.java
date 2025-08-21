package com.example.ledgerly.controller;

import com.example.ledgerly.dto.PurchaseRequest;
import com.example.ledgerly.entity.Customer;
import com.example.ledgerly.entity.LedgerEntry;
import com.example.ledgerly.entity.Shop;
import com.example.ledgerly.entity.TransactionType;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.repository.CustomerRepository;
import com.example.ledgerly.repository.LedgerEntryRepository;
import com.example.ledgerly.repository.ShopRepository;
import com.example.ledgerly.service.AuditService;
import com.example.ledgerly.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for managing customer purchases (creates DEBIT ledger entries)
 */
@RestController
@RequestMapping("/api/v1/ledger")
@Tag(name = "Purchase", description = "Purchase management endpoints")
@PreAuthorize("hasAnyRole('ADMIN', 'OWNER', 'STAFF')")
public class PurchaseController {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseController.class);

    private final CustomerRepository customerRepository;
    private final ShopRepository shopRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final UserService userService;
    private final AuditService auditService;

    @Autowired
    public PurchaseController(CustomerRepository customerRepository,
                            ShopRepository shopRepository,
                            LedgerEntryRepository ledgerEntryRepository,
                            UserService userService,
                            AuditService auditService) {
        this.customerRepository = customerRepository;
        this.shopRepository = shopRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
        this.userService = userService;
        this.auditService = auditService;
    }

    /**
     * Record a customer purchase (creates DEBIT ledger entry)
     */
    @PostMapping("/debit")
    @Operation(
        summary = "Record customer purchase",
        description = "Record a customer purchase or service that creates a DEBIT ledger entry"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Purchase recorded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Customer or Shop not found")
    })
    public ResponseEntity<Map<String, Object>> recordPurchase(
            @Parameter(description = "Purchase details", required = true)
            @Valid @RequestBody PurchaseRequest request) {
        
        logger.info("Purchase request received for customer: {}, shop: {}, amount: {}", 
                   request.getCustomerId(), request.getShopId(), request.getAmount());
        
        try {
            // Validate customer exists
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElse(null);
            if (customer == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Customer not found with ID: " + request.getCustomerId());
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Validate shop exists
            Shop shop = shopRepository.findById(request.getShopId())
                    .orElse(null);
            if (shop == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Shop not found with ID: " + request.getShopId());
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Get current authenticated user
            User currentUser = getCurrentUser();

            // Create DEBIT ledger entry
            LedgerEntry debitEntry = new LedgerEntry();
            debitEntry.setCustomer(customer);
            debitEntry.setShop(shop);
            debitEntry.setTransactionDate(request.getTransactionDate());
            debitEntry.setDescription(request.getDescription());
            debitEntry.setAmount(request.getAmount());
            debitEntry.setTransactionType(TransactionType.DEBIT); // This creates debt
            debitEntry.setReferenceNumber("PUR-" + System.currentTimeMillis());
            // Category is not stored in LedgerEntry entity
            debitEntry.setNotes(request.getNotes());
            debitEntry.setActive(true);
            debitEntry.setCreatedBy(currentUser);
            debitEntry.setUpdatedBy(currentUser);
            debitEntry.setCreatedAt(LocalDateTime.now());
            debitEntry.setUpdatedAt(LocalDateTime.now());

            // Save the ledger entry
            LedgerEntry savedEntry = ledgerEntryRepository.save(debitEntry);

            // Audit logging
            Map<String, Object> auditSnapshot = auditService.createAuditSnapshot(savedEntry);
            auditService.logSuccess("CREATE_DEBIT_ENTRY", "LEDGER_ENTRY", savedEntry.getId(),
                    null, auditSnapshot,
                    String.format("Created DEBIT ledger entry for purchase: %s - Amount: %s", 
                                request.getDescription(), request.getAmount()),
                    currentUser);

            logger.info("Purchase recorded successfully. Ledger entry ID: {}", savedEntry.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Purchase recorded successfully");
            response.put("ledgerEntryId", savedEntry.getId());
            response.put("referenceNumber", savedEntry.getReferenceNumber());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error recording purchase: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to record purchase: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Get current authenticated user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }
}
