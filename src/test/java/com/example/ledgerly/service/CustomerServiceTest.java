package com.example.ledgerly.service;

import com.example.ledgerly.dto.CustomerCreateRequest;
import com.example.ledgerly.dto.CustomerResponse;
import com.example.ledgerly.dto.CustomerUpdateRequest;
import com.example.ledgerly.entity.Customer;
import com.example.ledgerly.entity.RelationshipType;
import com.example.ledgerly.entity.Shop;
import com.example.ledgerly.entity.StaffShopMapping;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.entity.Role;
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

    private Customer testCustomer;
    private CustomerCreateRequest createRequest;
    private CustomerUpdateRequest updateRequest;
    private User testUser;
    private Shop testShop;
    private StaffShopMapping staffShopMapping;

    @BeforeEach
    void setUp() {
        testShop = new Shop();
        testShop.setId(1L);
        testShop.setName("Test Shop");
        testShop.setAddress("Test Address");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRole(Role.STAFF);

        staffShopMapping = new StaffShopMapping();
        staffShopMapping.setId(1L);
        staffShopMapping.setStaff(testUser);
        staffShopMapping.setShop(testShop);
        staffShopMapping.setActive(true);

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("customer@example.com");
        testCustomer.setPhoneNumber("1234567890");
        testCustomer.setAddress("Customer Address");
        testCustomer.setBusinessName("Test Business");
        testCustomer.setGstNumber("GST123456");
        testCustomer.setPanNumber("PAN123456");
        testCustomer.setRelationshipType(RelationshipType.CUSTOMER);
        testCustomer.setNotes("Test notes");
        testCustomer.setCreditLimit(new BigDecimal("1000.00"));
        testCustomer.setCurrentBalance(BigDecimal.ZERO);
        testCustomer.setShop(testShop);
        testCustomer.setCreatedBy(testUser);
        testCustomer.setCreatedAt(LocalDateTime.now());
        testCustomer.setActive(true);

        createRequest = new CustomerCreateRequest();
        createRequest.setName("New Customer");
        createRequest.setEmail("new@example.com");
        createRequest.setPhoneNumber("9876543210");
        createRequest.setAddress("New Address");
        createRequest.setBusinessName("New Business");
        createRequest.setGstNumber("GST789012");
        createRequest.setPanNumber("PAN789012");
        createRequest.setRelationshipType(RelationshipType.CUSTOMER);
        createRequest.setNotes("New customer notes");
        createRequest.setCreditLimit(new BigDecimal("500.00"));

        updateRequest = new CustomerUpdateRequest();
        updateRequest.setName("Updated Customer");
        updateRequest.setEmail("updated@example.com");
        updateRequest.setPhoneNumber("5555555555");
        updateRequest.setAddress("Updated Address");
        updateRequest.setBusinessName("Updated Business");
        updateRequest.setGstNumber("GST555555");
        updateRequest.setPanNumber("PAN555555");
        updateRequest.setRelationshipType(RelationshipType.SUPPLIER);
        updateRequest.setNotes("Updated notes");
        updateRequest.setCreditLimit(new BigDecimal("750.00"));
    }

    @Test
    void testCreateCustomerSuccess() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(staffShopMappingRepository.findByStaffId(anyLong())).thenReturn(Optional.of(staffShopMapping));
        when(customerRepository.existsByEmailAndShopId(anyString(), anyLong())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerResponse result = customerService.createCustomer(createRequest, "testuser");

        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getId());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testCreateCustomerUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> customerService.createCustomer(createRequest, "nonexistent"));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testCreateCustomerStaffNotAssignedToShop() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(staffShopMappingRepository.findByStaffId(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> customerService.createCustomer(createRequest, "testuser"));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testCreateCustomerEmailAlreadyExists() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(staffShopMappingRepository.findByStaffId(anyLong())).thenReturn(Optional.of(staffShopMapping));
        when(customerRepository.existsByEmailAndShopId(anyString(), anyLong())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> customerService.createCustomer(createRequest, "testuser"));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testGetCustomerByIdSuccess() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(staffShopMappingRepository.findByStaffId(anyLong())).thenReturn(Optional.of(staffShopMapping));
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(testCustomer));

        CustomerResponse result = customerService.getCustomerById(1L, "testuser");

        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getId());
    }

    @Test
    void testGetCustomerByIdNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(staffShopMappingRepository.findByStaffId(anyLong())).thenReturn(Optional.of(staffShopMapping));
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> customerService.getCustomerById(1L, "testuser"));
    }

    @Test
    void testGetAllCustomersSuccess() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(staffShopMappingRepository.findByStaffId(anyLong())).thenReturn(Optional.of(staffShopMapping));
        when(customerRepository.findByShopIdAndIsActiveTrue(anyLong())).thenReturn(java.util.List.of(testCustomer));

        java.util.List<CustomerResponse> result = customerService.getAllCustomers("testuser");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCustomer.getId(), result.get(0).getId());
    }

    @Test
    void testUpdateCustomerSuccess() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(staffShopMappingRepository.findByStaffId(anyLong())).thenReturn(Optional.of(staffShopMapping));
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        CustomerResponse result = customerService.updateCustomer(1L, updateRequest, "testuser");

        assertNotNull(result);
        assertEquals(testCustomer.getId(), result.getId());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomerNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(staffShopMappingRepository.findByStaffId(anyLong())).thenReturn(Optional.of(staffShopMapping));
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> customerService.updateCustomer(1L, updateRequest, "testuser"));
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testDeleteCustomerSuccess() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(staffShopMappingRepository.findByStaffId(anyLong())).thenReturn(Optional.of(staffShopMapping));
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        customerService.deleteCustomer(1L, "testuser");

        assertFalse(testCustomer.isActive());
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void testDeleteCustomerNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));
        when(staffShopMappingRepository.findByStaffId(anyLong())).thenReturn(Optional.of(staffShopMapping));
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> customerService.deleteCustomer(1L, "testuser"));
        verify(customerRepository, never()).save(any(Customer.class));
    }
}
