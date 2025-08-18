package com.example.ledgerly.controller;

import com.example.ledgerly.service.BusinessRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Business Rules management
 */
@RestController
@RequestMapping("/api/v1/business-rules")
@PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
public class BusinessRuleController {

    private final BusinessRuleService businessRuleService;

    @Autowired
    public BusinessRuleController(BusinessRuleService businessRuleService) {
        this.businessRuleService = businessRuleService;
    }

    /**
     * Get current business rule configuration
     */
    @GetMapping("/configuration")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> getBusinessRuleConfiguration() {
        try {
            BusinessRuleService.BusinessRuleConfiguration config = businessRuleService.getConfiguration();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("configuration", Map.of(
                    "allowNegativeBalance", config.isAllowNegativeBalance(),
                    "maxTransactionAmount", config.getMaxTransactionAmount(),
                    "minTransactionAmount", config.getMinTransactionAmount(),
                    "maxDailyTransactionLimit", config.getMaxDailyTransactionLimit(),
                    "requireFutureDateValidation", config.isRequireFutureDateValidation()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch business rule configuration: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Get business rule descriptions
     */
    @GetMapping("/descriptions")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
    public ResponseEntity<Map<String, Object>> getBusinessRuleDescriptions() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("rules", Map.of(
                    "NEGATIVE_BALANCE_PREVENTION", Map.of(
                            "code", "INSUFFICIENT_BALANCE",
                            "description", "Prevents transactions that would result in negative customer balances",
                            "configurable", true
                    ),
                    "TRANSACTION_AMOUNT_VALIDATION", Map.of(
                            "code", "INVALID_AMOUNT",
                            "description", "Validates transaction amounts are within acceptable ranges",
                            "rules", Map.of(
                                    "minAmount", "Minimum transaction amount (configurable)",
                                    "maxAmount", "Maximum transaction amount (configurable)",
                                    "positiveOnly", "Amount must be greater than zero",
                                    "decimalPlaces", "Maximum 2 decimal places allowed"
                            )
                    ),
                    "CREDIT_LIMIT_VALIDATION", Map.of(
                            "code", "CREDIT_LIMIT_EXCEEDED",
                            "description", "Ensures customer credit limits are not exceeded",
                            "configurable", false
                    ),
                    "DAILY_TRANSACTION_LIMIT", Map.of(
                            "code", "DAILY_LIMIT_EXCEEDED",
                            "description", "Limits total daily transaction amounts per customer",
                            "configurable", true
                    ),
                    "DATE_VALIDATION", Map.of(
                            "code", "INVALID_DATE",
                            "description", "Validates transaction dates are reasonable",
                            "rules", Map.of(
                                    "futureDate", "Future dates may be prohibited (configurable)",
                                    "pastLimit", "Dates older than 1 year are prohibited"
                            )
                    ),
                    "RECONCILIATION_PROTECTION", Map.of(
                            "code", "RECONCILED_ENTRY_MODIFICATION",
                            "description", "Prevents modification of reconciled entries",
                            "configurable", false
                    ),
                    "CUSTOMER_STATUS_VALIDATION", Map.of(
                            "code", "INACTIVE_CUSTOMER",
                            "description", "Prevents transactions for inactive customers",
                            "configurable", false
                    ),
                    "DELETION_PROTECTION", Map.of(
                            "code", "OLD_ENTRY_DELETION",
                            "description", "Restricts deletion of old entries to administrators",
                            "rules", Map.of(
                                    "timeLimit", "30 days",
                                    "adminOverride", "Administrators can delete any entry"
                            )
                    )
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch business rule descriptions: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
