package com.example.ledgerly.service;

import com.example.ledgerly.dto.CustomerCreateRequest;
import com.example.ledgerly.dto.CustomerResponse;
import com.example.ledgerly.dto.CustomerUpdateRequest;
import com.example.ledgerly.entity.*;
import com.example.ledgerly.repository.CustomerRepository;
import com.example.ledgerly.repository.StaffShopMappingRepository;
import com.example.ledgerly.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StaffShopMappingRepository staffShopMappingRepository;

    @InjectMocks
    private CustomerService customerService;

    private User staffUser;
    private User ownerUser;
    private Shop testShop;
    private StaffShopMapping staffShopMapping;
    private Customer testCustomer;
    private CustomerCreateRequest createRequest;
    private CustomerUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        // Setup test shop
        testShop = new Shop();
        testShop.setId(1L);
        testShop.setName("Test Shop");
        testShop.setDescription("Test Shop Description");
        testShop.setAddress("123 Test Street");
        testShop.setCity("Test City");
        testShop.setState("Test State");
        testShop.setPincode("123456");
        testShop.setActive(true);

        // Setup staff user
        staffUser = new User();
        staffUser.setId(1L);
        staffUser.setUsername("staffuser");
        staffUser.setEmail("staff@test.com");
        staffUser.setRole(Role.STAFF);
        staffUser.setActive(true);

        // Setup owner user
        ownerUser = new User();
        ownerUser.setId(2L);
        ownerUser.setUsername("owneruser");
        ownerUser.setEmail("owner@test.com");
        ownerUser.setRole(Role.OWNER);
        ownerUser.setActive(true);

        // Setup staff-shop mapping
        staffShopMapping = new StaffShopMapping();
        staffShopMapping.setId(1L);
        staffShopMapping.setStaff(staffUser);
        staffShopMapping.setShop(testShop);
        staffShopMapping.setActive(true);

        // Setup test customer
        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("customer@test.com");
        testCustomer.setPhoneNumber("1234567890");
        testCustomer.setAddress("456 Customer Street");
        testCustomer.setBusinessName("Test Business");
        testCustomer.setGstNumber("GST123456");
        testCustomer.setPanNumber("PAN123456");
        testCustomer.setRelationshipType(RelationshipType.CUSTOMER);
        testCustomer.setNotes("Test notes");
        testCustomer.setCreditLimit(BigDecimal.valueOf(10000));
        testCustomer.setCurrentBalance(BigDecimal.ZERO);
        testCustomer.setCreatedBy(staffUser);
        testCustomer.setShop(testShop);
        testCustomer.setActive(true);
        testCustomer.setCreatedAt(LocalDateTime.now());

        // Setup create request
        createRequest = new CustomerCreateRequest();
        createRequest.setName("New Customer");
        createRequest.setEmail("newcustomer@test.com");
        createRequest.setPhoneNumber("9876543210");
        createRequest.setAddress("789 New Street");
        createRequest.setBusinessName("New Business");
        createRequest.setGstNumber("GST789012");
        createRequest.setPanNumber("PAN789012");
        createRequest.setRelationshipType(RelationshipType.CUSTOMER);
        createRequest.setNotes("New customer notes");
        createRequest.setCreditLimit(BigDecimal.valueOf(15000));

        // Setup update request
        updateRequest = new CustomerUpdateRequest();
        updateRequest.setName("Updated Customer");
        updateRequest.setEmail("updated@test.com");
        updateRequest.setPhoneNumber("1111111111");
    }

    @Test
    void testCreateCustomer_Success_Staff() {
        // Arrange
        when(userRepository.findByUsername("staffuser")).thenReturn(Optional.of(staffUser));
        when(staffShopMappingRepository.findByStaffId(1L)).thenReturn(Optional.of(staffShopMapping));
        when(customerRepository.existsByEmailAndShopId(anyString(), eq(1L))).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        CustomerResponse response = customerService.createCustomer(createRequest, "staffuser");

        // Assert
        assertNotNull(response);
        assertEquals(testCustomer.getId(), response.getId());
        assertEquals(testCustomer.getName(), response.getName());
        assertEquals(testCustomer.getEmail(), response.getEmail());
        assertEquals(testCustomer.getShop().getId(), response.getShopId());
        assertEquals(testCustomer.getShop().getName(), response.getShopName());

        verify(userRepository).findByUsername("staffuser");
        verify(staffShopMappingRepository).findByStaffId(1L);
        verify(customerRepository).existsByEmailAndShopId(createRequest.getEmail(), 1L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_OwnerNotAllowed() {
        // Arrange
        when(userRepository.findByUsername("owneruser")).thenReturn(Optional.of(ownerUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> customerService.createCustomer(createRequest, "owneruser"));
        assertEquals("Owners must specify shop when creating customers", exception.getMessage());

        verify(userRepository).findByUsername("owneruser");
        verify(staffShopMappingRepository, never()).findByStaffId(anyLong());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_StaffNotAssignedToShop() {
        // Arrange
        when(userRepository.findByUsername("staffuser")).thenReturn(Optional.of(staffUser));
        when(staffShopMappingRepository.findByStaffId(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> customerService.createCustomer(createRequest, "staffuser"));
        assertEquals("Staff is not assigned to any shop", exception.getMessage());

        verify(userRepository).findByUsername("staffuser");
        verify(staffShopMappingRepository).findByStaffId(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_EmailAlreadyExists() {
        // Arrange
        when(userRepository.findByUsername("staffuser")).thenReturn(Optional.of(staffUser));
        when(staffShopMappingRepository.findByStaffId(1L)).thenReturn(Optional.of(staffShopMapping));
        when(customerRepository.existsByEmailAndShopId(anyString(), eq(1L))).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> customerService.createCustomer(createRequest, "staffuser"));
        assertEquals("Customer with this email already exists in this shop", exception.getMessage());

        verify(userRepository).findByUsername("staffuser");
        verify(staffShopMappingRepository).findByStaffId(1L);
        verify(customerRepository).existsByEmailAndShopId(createRequest.getEmail(), 1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testCreateCustomerInShop_Success_Owner() {
        // Arrange
        when(userRepository.findByUsername("owneruser")).thenReturn(Optional.of(ownerUser));
        when(customerRepository.existsByEmailAndShopId(anyString(), eq(1L))).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        CustomerResponse response = customerService.createCustomerInShop(createRequest, "owneruser", 1L);

        // Assert
        assertNotNull(response);
        assertEquals(testCustomer.getId(), response.getId());
        assertEquals(testCustomer.getName(), response.getName());

        verify(userRepository).findByUsername("owneruser");
        verify(customerRepository).existsByEmailAndShopId(createRequest.getEmail(), 1L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testCreateCustomerInShop_StaffNotAllowed() {
        // Arrange
        when(userRepository.findByUsername("staffuser")).thenReturn(Optional.of(staffUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> customerService.createCustomerInShop(createRequest, "staffuser", 1L));
        assertEquals("Only owners and admins can create customers in specific shops", exception.getMessage());

        verify(userRepository).findByUsername("staffuser");
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_Success() {
        // Arrange
        when(userRepository.findByUsername("staffuser")).thenReturn(Optional.of(staffUser));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(staffShopMappingRepository.findByStaffId(1L)).thenReturn(Optional.of(staffShopMapping));
        when(customerRepository.existsByEmailAndShopId(anyString(), eq(1L))).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        CustomerResponse response = customerService.updateCustomer(1L, updateRequest, "staffuser");

        // Assert
        assertNotNull(response);
        assertEquals(testCustomer.getId(), response.getId());

        verify(userRepository).findByUsername("staffuser");
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer_CustomerNotFound() {
        // Arrange
        when(userRepository.findByUsername("staffuser")).thenReturn(Optional.of(staffUser));
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> customerService.updateCustomer(1L, updateRequest, "staffuser"));
        assertEquals("Customer not found", exception.getMessage());

        verify(userRepository).findByUsername("staffuser");
        verify(customerRepository).findById(1L);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testGetCustomerById_Success() {
        // Arrange
        when(userRepository.findByUsername("staffuser")).thenReturn(Optional.of(staffUser));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(staffShopMappingRepository.findByStaffId(1L)).thenReturn(Optional.of(staffShopMapping));

        // Act
        CustomerResponse response = customerService.getCustomerById(1L, "staffuser");

        // Assert
        assertNotNull(response);
        assertEquals(testCustomer.getId(), response.getId());
        assertEquals(testCustomer.getName(), response.getName());

        verify(userRepository).findByUsername("staffuser");
        verify(customerRepository).findById(1L);
    }

    @Test
    void testGetAllCustomers_Staff() {
        // Arrange
        when(userRepository.findByUsername("staffuser")).thenReturn(Optional.of(staffUser));
        when(staffShopMappingRepository.findByStaffId(1L)).thenReturn(Optional.of(staffShopMapping));
        when(customerRepository.findByShopIdAndIsActiveTrue(1L)).thenReturn(List.of(testCustomer));

        // Act
        List<CustomerResponse> response = customerService.getAllCustomers("staffuser");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(testCustomer.getId(), response.get(0).getId());

        verify(userRepository).findByUsername("staffuser");
        verify(staffShopMappingRepository).findByStaffId(1L);
        verify(customerRepository).findByShopIdAndIsActiveTrue(1L);
    }

    @Test
    void testGetAllCustomers_Owner() {
        // Arrange
        when(userRepository.findByUsername("owneruser")).thenReturn(Optional.of(ownerUser));
        when(customerRepository.findByShopOwnerIdAndIsActiveTrue(2L)).thenReturn(List.of(testCustomer));

        // Act
        List<CustomerResponse> response = customerService.getAllCustomers("owneruser");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(testCustomer.getId(), response.get(0).getId());

        verify(userRepository).findByUsername("owneruser");
        verify(customerRepository).findByShopOwnerIdAndIsActiveTrue(2L);
    }

    @Test
    void testDeleteCustomer_Success() {
        // Arrange
        when(userRepository.findByUsername("staffuser")).thenReturn(Optional.of(staffUser));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(staffShopMappingRepository.findByStaffId(1L)).thenReturn(Optional.of(staffShopMapping));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        customerService.deleteCustomer(1L, "staffuser");

        // Assert
        verify(userRepository).findByUsername("staffuser");
        verify(customerRepository).findById(1L);
        verify(customerRepository).save(testCustomer);
        assertFalse(testCustomer.isActive());
    }

    @Test
    void testSearchCustomers_Staff() {
        // Arrange
        when(userRepository.findByUsername("staffuser")).thenReturn(Optional.of(staffUser));
        when(staffShopMappingRepository.findByStaffId(1L)).thenReturn(Optional.of(staffShopMapping));
        when(customerRepository.findByShopIdAndNameContainingIgnoreCaseAndIsActiveTrue(eq(1L), eq("test")))
            .thenReturn(List.of(testCustomer));

        // Act
        List<CustomerResponse> response = customerService.searchCustomers("test", "staffuser");

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(testCustomer.getId(), response.get(0).getId());

        verify(userRepository).findByUsername("staffuser");
        verify(staffShopMappingRepository).findByStaffId(1L);
        verify(customerRepository).findByShopIdAndNameContainingIgnoreCaseAndIsActiveTrue(1L, "test");
    }

    @Test
    void testGetCustomerStatistics_Staff() {
        // Arrange
        when(userRepository.findByUsername("staffuser")).thenReturn(Optional.of(staffUser));
        when(staffShopMappingRepository.findByStaffId(1L)).thenReturn(Optional.of(staffShopMapping));
        when(customerRepository.countByShopIdAndIsActiveTrue(1L)).thenReturn(5L);
        when(customerRepository.sumCurrentBalanceByShopIdAndIsActiveTrue(1L)).thenReturn(BigDecimal.valueOf(5000));

        // Act
        CustomerService.CustomerStatistics stats = customerService.getCustomerStatistics("staffuser");

        // Assert
        assertNotNull(stats);
        assertEquals(5L, stats.getTotalCustomers());
        assertEquals(BigDecimal.valueOf(5000), stats.getTotalBalance());

        verify(userRepository).findByUsername("staffuser");
        verify(staffShopMappingRepository).findByStaffId(1L);
        verify(customerRepository).countByShopIdAndIsActiveTrue(1L);
        verify(customerRepository).sumCurrentBalanceByShopIdAndIsActiveTrue(1L);
    }

    @Test
    void testValidateShopAccess_StaffAccessDenied() {
        // Arrange
        Shop otherShop = new Shop();
        otherShop.setId(2L);
        otherShop.setName("Other Shop");
        
        Customer otherCustomer = new Customer();
        otherCustomer.setId(2L);
        otherCustomer.setName("Other Customer");
        otherCustomer.setShop(otherShop);

        when(userRepository.findByUsername("staffuser")).thenReturn(Optional.of(staffUser));
        when(customerRepository.findById(2L)).thenReturn(Optional.of(otherCustomer));
        when(staffShopMappingRepository.findByStaffId(1L)).thenReturn(Optional.of(staffShopMapping));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> customerService.getCustomerById(2L, "staffuser"));
        assertEquals("Access denied: You can only access customers from your assigned shop", exception.getMessage());

        verify(userRepository).findByUsername("staffuser");
        verify(customerRepository).findById(2L);
        verify(staffShopMappingRepository).findByStaffId(1L);
    }
}
