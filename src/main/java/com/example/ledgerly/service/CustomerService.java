package com.example.ledgerly.service;

import com.example.ledgerly.dto.CustomerCreateRequest;
import com.example.ledgerly.dto.CustomerResponse;
import com.example.ledgerly.dto.CustomerUpdateRequest;
import com.example.ledgerly.entity.Customer;
import com.example.ledgerly.entity.RelationshipType;
import com.example.ledgerly.entity.Shop;
import com.example.ledgerly.entity.StaffShopMapping;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.repository.CustomerRepository;
import com.example.ledgerly.repository.StaffShopMappingRepository;
import com.example.ledgerly.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final StaffShopMappingRepository staffShopMappingRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, 
                         UserRepository userRepository,
                         StaffShopMappingRepository staffShopMappingRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.staffShopMappingRepository = staffShopMappingRepository;
    }

    /**
     * Create a new customer with automatic shop assignment
     */
    public CustomerResponse createCustomer(CustomerCreateRequest request, String username) {
        User createdBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get the shop for staff users
        Shop customerShop = null;
        if (createdBy.getRole().name().equals("STAFF")) {
            StaffShopMapping mapping = staffShopMappingRepository.findByStaffId(createdBy.getId())
                    .orElseThrow(() -> new RuntimeException("Staff is not assigned to any shop"));
            customerShop = mapping.getShop();
        } else if (createdBy.getRole().name().equals("OWNER")) {
            // For owners, they need to specify which shop to create customer in
            // This will be handled by the controller
            throw new RuntimeException("Owners must specify shop when creating customers");
        }

        // Check if customer email already exists in the same shop
        if (customerShop != null && customerRepository.existsByEmailAndShopId(request.getEmail(), customerShop.getId())) {
            throw new RuntimeException("Customer with this email already exists in this shop");
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setAddress(request.getAddress());
        customer.setBusinessName(request.getBusinessName());
        customer.setGstNumber(request.getGstNumber());
        customer.setPanNumber(request.getPanNumber());
        customer.setRelationshipType(request.getRelationshipType());
        customer.setNotes(request.getNotes());
        customer.setCreditLimit(request.getCreditLimit() != null ? request.getCreditLimit() : BigDecimal.ZERO);
        customer.setCurrentBalance(BigDecimal.ZERO);
        customer.setCreatedBy(createdBy);
        customer.setShop(customerShop);

        Customer savedCustomer = customerRepository.save(customer);
        return mapToResponse(savedCustomer);
    }

    /**
     * Create a customer in a specific shop (for owners)
     */
    public CustomerResponse createCustomerInShop(CustomerCreateRequest request, String username, Long shopId) {
        User createdBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify the user owns this shop or is admin
        if (!createdBy.getRole().name().equals("OWNER") && !createdBy.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Only owners and admins can create customers in specific shops");
        }

        // Check if customer email already exists in the same shop
        if (customerRepository.existsByEmailAndShopId(request.getEmail(), shopId)) {
            throw new RuntimeException("Customer with this email already exists in this shop");
        }

        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setAddress(request.getAddress());
        customer.setBusinessName(request.getBusinessName());
        customer.setGstNumber(request.getGstNumber());
        customer.setPanNumber(request.getPanNumber());
        customer.setRelationshipType(request.getRelationshipType());
        customer.setNotes(request.getNotes());
        customer.setCreditLimit(request.getCreditLimit() != null ? request.getCreditLimit() : BigDecimal.ZERO);
        customer.setCurrentBalance(BigDecimal.ZERO);
        customer.setCreatedBy(createdBy);

        Customer savedCustomer = customerRepository.save(customer);
        return mapToResponse(savedCustomer);
    }

    /**
     * Update customer with shop validation
     */
    public CustomerResponse updateCustomer(Long customerId, CustomerUpdateRequest request, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Validate shop access
        validateShopAccess(currentUser, customer.getShop());

        // Check if email is being changed and if it already exists in the same shop
        if (!customer.getEmail().equals(request.getEmail()) && 
            customerRepository.existsByEmailAndShopId(request.getEmail(), customer.getShop().getId())) {
            throw new RuntimeException("Customer with this email already exists in this shop");
        }

        // Update fields
        if (request.getName() != null) {
            customer.setName(request.getName());
        }
        if (request.getEmail() != null) {
            customer.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null) {
            customer.setPhoneNumber(request.getPhoneNumber());
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

        Customer updatedCustomer = customerRepository.save(customer);
        return mapToResponse(updatedCustomer);
    }

    /**
     * Get customer by ID with shop validation
     */
    public CustomerResponse getCustomerById(Long customerId, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Validate shop access
        validateShopAccess(currentUser, customer.getShop());

        return mapToResponse(customer);
    }

    /**
     * Get all customers for current user (filtered by shop)
     */
    public List<CustomerResponse> getAllCustomers(String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Customer> customers;
        if (currentUser.getRole().name().equals("STAFF")) {
            // Staff can only see customers from their assigned shop
            StaffShopMapping mapping = staffShopMappingRepository.findByStaffId(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Staff is not assigned to any shop"));
            customers = customerRepository.findByShopIdAndIsActiveTrue(mapping.getShop().getId());
        } else if (currentUser.getRole().name().equals("OWNER")) {
            // Owners can see customers from all their shops
            customers = customerRepository.findByShopOwnerIdAndIsActiveTrue(currentUser.getId());
        } else if (currentUser.getRole().name().equals("ADMIN")) {
            // Admins can see all customers
            customers = customerRepository.findByIsActiveTrue();
        } else {
            throw new RuntimeException("Invalid user role");
        }

        return customers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get customers by shop ID with validation
     */
    public List<CustomerResponse> getCustomersByShop(Long shopId, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate shop access
        validateShopAccess(currentUser, shopId);

        List<Customer> customers = customerRepository.findByShopIdAndIsActiveTrue(shopId);
        return customers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Delete customer with shop validation
     */
    public void deleteCustomer(Long customerId, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Validate shop access
        validateShopAccess(currentUser, customer.getShop());

        // Soft delete
        customer.setActive(false);
        customerRepository.save(customer);
    }

    /**
     * Search customers with shop filtering
     */
    public List<CustomerResponse> searchCustomers(String searchTerm, String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Customer> customers;
        if (currentUser.getRole().name().equals("STAFF")) {
            // Staff can only search in their assigned shop
            StaffShopMapping mapping = staffShopMappingRepository.findByStaffId(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Staff is not assigned to any shop"));
            customers = customerRepository.findByShopIdAndNameContainingIgnoreCaseAndIsActiveTrue(
                    mapping.getShop().getId(), searchTerm);
        } else if (currentUser.getRole().name().equals("OWNER")) {
            // Owners can search in all their shops
            customers = customerRepository.findByShopOwnerIdAndNameContainingIgnoreCaseAndIsActiveTrue(
                    currentUser.getId(), searchTerm);
        } else if (currentUser.getRole().name().equals("ADMIN")) {
            // Admins can search in all shops
            customers = customerRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(searchTerm);
        } else {
            throw new RuntimeException("Invalid user role");
        }

        return customers.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get customer statistics with shop filtering
     */
    public CustomerStatistics getCustomerStatistics(String username) {
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CustomerStatistics stats = new CustomerStatistics();
        
        if (currentUser.getRole().name().equals("STAFF")) {
            // Staff statistics for their assigned shop
            StaffShopMapping mapping = staffShopMappingRepository.findByStaffId(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Staff is not assigned to any shop"));
            stats.setTotalCustomers(customerRepository.countByShopIdAndIsActiveTrue(mapping.getShop().getId()));
            stats.setTotalBalance(customerRepository.sumCurrentBalanceByShopIdAndIsActiveTrue(mapping.getShop().getId()));
        } else if (currentUser.getRole().name().equals("OWNER")) {
            // Owner statistics for all their shops
            stats.setTotalCustomers(customerRepository.countByShopOwnerIdAndIsActiveTrue(currentUser.getId()));
            stats.setTotalBalance(customerRepository.sumCurrentBalanceByShopOwnerIdAndIsActiveTrue(currentUser.getId()));
        } else if (currentUser.getRole().name().equals("ADMIN")) {
            // Admin statistics for all shops
            stats.setTotalCustomers(customerRepository.countByIsActiveTrue());
            stats.setTotalBalance(customerRepository.sumCurrentBalanceByIsActiveTrue());
        }

        return stats;
    }

    /**
     * Validate shop access for current user
     */
    private void validateShopAccess(User currentUser, Shop shop) {
        if (currentUser.getRole().name().equals("ADMIN")) {
            return; // Admin can access all shops
        }
        
        if (currentUser.getRole().name().equals("OWNER")) {
            if (!shop.getOwner().getId().equals(currentUser.getId())) {
                throw new RuntimeException("Access denied: You don't own this shop");
            }
        } else if (currentUser.getRole().name().equals("STAFF")) {
            StaffShopMapping mapping = staffShopMappingRepository.findByStaffId(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Staff is not assigned to any shop"));
            if (!mapping.getShop().getId().equals(shop.getId())) {
                throw new RuntimeException("Access denied: You can only access customers from your assigned shop");
            }
        }
    }

    /**
     * Validate shop access by shop ID
     */
    private void validateShopAccess(User currentUser, Long shopId) {
        if (currentUser.getRole().name().equals("ADMIN")) {
            return; // Admin can access all shops
        }
        
        if (currentUser.getRole().name().equals("OWNER")) {
            // Owner can access their own shops
            // This validation will be done at the controller level
        } else if (currentUser.getRole().name().equals("STAFF")) {
            StaffShopMapping mapping = staffShopMappingRepository.findByStaffId(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Staff is not assigned to any shop"));
            if (!mapping.getShop().getId().equals(shopId)) {
                throw new RuntimeException("Access denied: You can only access customers from your assigned shop");
            }
        }
    }

    private CustomerResponse mapToResponse(Customer customer) {
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
        
        if (customer.getShop() != null) {
            response.setShopId(customer.getShop().getId());
            response.setShopName(customer.getShop().getName());
        }
        
        return response;
    }

    // Statistics class
    public static class CustomerStatistics {
        private long totalCustomers;
        private BigDecimal totalBalance;

        public long getTotalCustomers() { return totalCustomers; }
        public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }
        public BigDecimal getTotalBalance() { return totalBalance; }
        public void setTotalBalance(BigDecimal totalBalance) { this.totalBalance = totalBalance; }
    }
}
