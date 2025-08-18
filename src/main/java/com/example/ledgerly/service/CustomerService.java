package com.example.ledgerly.service;

import com.example.ledgerly.dto.CustomerCreateRequest;
import com.example.ledgerly.dto.CustomerResponse;
import com.example.ledgerly.dto.CustomerUpdateRequest;
import com.example.ledgerly.entity.Customer;
import com.example.ledgerly.entity.RelationshipType;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.repository.CustomerRepository;
import com.example.ledgerly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Customer entity operations
 */
@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    /**
     * Create a new customer
     */
    public CustomerResponse createCustomer(CustomerCreateRequest request) {
        // Get current user
        User currentUser = getCurrentUser();

        // Check if email already exists
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            customerRepository.findByEmailIgnoreCase(request.getEmail().trim())
                    .ifPresent(customer -> {
                        throw new RuntimeException("Customer with email " + request.getEmail() + " already exists");
                    });
        }

        // Check if phone number already exists
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            customerRepository.findByPhoneNumber(request.getPhoneNumber().trim())
                    .ifPresent(customer -> {
                        throw new RuntimeException("Customer with phone number " + request.getPhoneNumber() + " already exists");
                    });
        }

        // Create new customer
        Customer customer = new Customer();
        customer.setName(request.getName().trim());
        customer.setEmail(request.getEmail() != null ? request.getEmail().trim() : null);
        customer.setPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber().trim() : null);
        customer.setAddress(request.getAddress());
        customer.setBusinessName(request.getBusinessName());
        customer.setGstNumber(request.getGstNumber());
        customer.setPanNumber(request.getPanNumber());
        customer.setRelationshipType(request.getRelationshipType());
        customer.setNotes(request.getNotes());
        customer.setCreditLimit(request.getCreditLimit());
        customer.setCreatedBy(currentUser);

        Customer savedCustomer = customerRepository.save(customer);
        return convertToResponse(savedCustomer);
    }

    /**
     * Get customer by ID
     */
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        return convertToResponse(customer);
    }

    /**
     * Update an existing customer
     */
    public CustomerResponse updateCustomer(Long id, CustomerUpdateRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));

        // Check if email already exists (excluding current customer)
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (customerRepository.existsByEmailIgnoreCaseAndIdNot(request.getEmail().trim(), id)) {
                throw new RuntimeException("Customer with email " + request.getEmail() + " already exists");
            }
        }

        // Check if phone number already exists (excluding current customer)
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().trim().isEmpty()) {
            if (customerRepository.existsByPhoneNumberAndIdNot(request.getPhoneNumber().trim(), id)) {
                throw new RuntimeException("Customer with phone number " + request.getPhoneNumber() + " already exists");
            }
        }

        // Update customer fields (only if provided)
        if (request.getName() != null) {
            customer.setName(request.getName().trim());
        }
        if (request.getEmail() != null) {
            customer.setEmail(request.getEmail().trim());
        }
        if (request.getPhoneNumber() != null) {
            customer.setPhoneNumber(request.getPhoneNumber().trim());
        }
        if (request.getAddress() != null) {
            customer.setAddress(request.getAddress());
        }
        if (request.getBusinessName() != null) {
            customer.setBusinessName(request.getBusinessName());
        }
        if (request.getGstNumber() != null) {
            customer.setGstNumber(request.getGstNumber());
        }
        if (request.getPanNumber() != null) {
            customer.setPanNumber(request.getPanNumber());
        }
        if (request.getRelationshipType() != null) {
            customer.setRelationshipType(request.getRelationshipType());
        }
        if (request.getNotes() != null) {
            customer.setNotes(request.getNotes());
        }
        if (request.getCreditLimit() != null) {
            customer.setCreditLimit(request.getCreditLimit());
        }
        if (request.getIsActive() != null) {
            customer.setActive(request.getIsActive());
        }

        Customer savedCustomer = customerRepository.save(customer);
        return convertToResponse(savedCustomer);
    }

    /**
     * Delete a customer (soft delete - mark as inactive)
     */
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        
        customer.setActive(false);
        customerRepository.save(customer);
    }

    /**
     * Get all customers with pagination
     */
    public Page<CustomerResponse> getAllCustomers(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Customer> customers = customerRepository.findAll(pageable);
        
        return customers.map(this::convertToResponse);
    }

    /**
     * Get active customers only
     */
    public List<CustomerResponse> getActiveCustomers() {
        List<Customer> customers = customerRepository.findByIsActiveTrue();
        return customers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search customers by multiple criteria
     */
    public Page<CustomerResponse> searchCustomers(String name, String email, String phoneNumber,
                                                RelationshipType relationshipType, Boolean isActive,
                                                String businessName, int page, int size,
                                                String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Customer> customers = customerRepository.searchCustomers(
                name, email, phoneNumber, relationshipType, isActive, businessName, pageable);
        
        return customers.map(this::convertToResponse);
    }

    /**
     * Get customers by relationship type
     */
    public List<CustomerResponse> getCustomersByRelationshipType(RelationshipType relationshipType) {
        List<Customer> customers = customerRepository.findByRelationshipTypeAndIsActive(relationshipType, true);
        return customers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search customers by name
     */
    public List<CustomerResponse> searchCustomersByName(String name) {
        List<Customer> customers = customerRepository.findByNameContainingIgnoreCase(name);
        return customers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get customers with outstanding balance
     */
    public List<CustomerResponse> getCustomersWithOutstandingBalance() {
        List<Customer> customers = customerRepository.findCustomersWithOutstandingBalance();
        return customers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get customer statistics
     */
    public CustomerStats getCustomerStats() {
        long totalCustomers = customerRepository.count();
        long activeCustomers = customerRepository.countByIsActiveTrue();
        long totalCustomerType = customerRepository.countByRelationshipType(RelationshipType.CUSTOMER);
        long totalSupplierType = customerRepository.countByRelationshipType(RelationshipType.SUPPLIER);
        
        return new CustomerStats(totalCustomers, activeCustomers, totalCustomerType, totalSupplierType);
    }

    /**
     * Convert Customer entity to CustomerResponse DTO
     */
    private CustomerResponse convertToResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setEmail(customer.getEmail());
        response.setPhoneNumber(customer.getPhoneNumber());
        response.setAddress(customer.getAddress());
        response.setBusinessName(customer.getBusinessName());
        response.setGstNumber(customer.getGstNumber());
        response.setPanNumber(customer.getPanNumber());
        response.setRelationshipType(customer.getRelationshipType());
        response.setNotes(customer.getNotes());
        response.setCreditLimit(customer.getCreditLimit());
        response.setCurrentBalance(customer.getCurrentBalance());
        response.setActive(customer.isActive());
        response.setCreatedAt(customer.getCreatedAt());
        response.setUpdatedAt(customer.getUpdatedAt());
        response.setCreatedByUsername(customer.getCreatedBy().getUsername());
        return response;
    }

    /**
     * Get current authenticated user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    /**
     * Inner class for customer statistics
     */
    public static class CustomerStats {
        private final long totalCustomers;
        private final long activeCustomers;
        private final long totalCustomerType;
        private final long totalSupplierType;

        public CustomerStats(long totalCustomers, long activeCustomers, long totalCustomerType, long totalSupplierType) {
            this.totalCustomers = totalCustomers;
            this.activeCustomers = activeCustomers;
            this.totalCustomerType = totalCustomerType;
            this.totalSupplierType = totalSupplierType;
        }

        public long getTotalCustomers() { return totalCustomers; }
        public long getActiveCustomers() { return activeCustomers; }
        public long getTotalCustomerType() { return totalCustomerType; }
        public long getTotalSupplierType() { return totalSupplierType; }
    }
}
