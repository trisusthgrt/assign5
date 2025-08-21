package com.example.ledgerly.service;

import com.example.ledgerly.entity.Customer;
import com.example.ledgerly.entity.LedgerEntry;
import com.example.ledgerly.entity.Shop;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.entity.Role;
import com.example.ledgerly.entity.TransactionType;
import com.example.ledgerly.repository.LedgerEntryRepository;
import com.example.ledgerly.repository.CustomerRepository;
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
class LedgerServiceTest {

    @Mock
    private LedgerEntryRepository ledgerEntryRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LedgerService ledgerService;

    private LedgerEntry testLedgerEntry;
    private Customer testCustomer;
    private User testUser;
    private Shop testShop;

    @BeforeEach
    void setUp() {
        testShop = new Shop();
        testShop.setId(1L);
        testShop.setName("Test Shop");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setRole(Role.STAFF);

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("Test Customer");
        testCustomer.setEmail("customer@example.com");
        testCustomer.setShop(testShop);
        testCustomer.setCurrentBalance(BigDecimal.ZERO);

        testLedgerEntry = new LedgerEntry();
        testLedgerEntry.setId(1L);
        testLedgerEntry.setAmount(new BigDecimal("100.00"));
        testLedgerEntry.setTransactionType(TransactionType.CREDIT);
        testLedgerEntry.setDescription("Test transaction");
        testLedgerEntry.setCustomer(testCustomer);
        testLedgerEntry.setShop(testShop);
        testLedgerEntry.setCreatedBy(testUser);
        testLedgerEntry.setCreatedAt(LocalDateTime.now());
        testLedgerEntry.setActive(true);
    }

    @Test
    void testGetLedgerEntryByIdSuccess() {
        when(ledgerEntryRepository.findById(anyLong())).thenReturn(Optional.of(testLedgerEntry));

        var result = ledgerService.getLedgerEntryById(1L);

        assertNotNull(result);
        assertEquals(testLedgerEntry.getId(), result.getId());
    }

    @Test
    void testGetLedgerEntryByIdNotFound() {
        when(ledgerEntryRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ledgerService.getLedgerEntryById(1L));
    }

    @Test
    void testGetCustomerBalanceSummarySuccess() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(testCustomer));

        var result = ledgerService.getCustomerBalanceSummary(1L);

        assertNotNull(result);
    }

    @Test
    void testGetCustomerBalanceSummaryCustomerNotFound() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> ledgerService.getCustomerBalanceSummary(1L));
    }
}
