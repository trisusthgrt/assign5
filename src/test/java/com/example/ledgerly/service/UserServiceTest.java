package com.example.ledgerly.service;

import com.example.ledgerly.dto.AuthResponse;
import com.example.ledgerly.dto.LoginRequest;
import com.example.ledgerly.dto.RegisterRequest;
import com.example.ledgerly.entity.Role;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.repository.UserRepository;
import com.example.ledgerly.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setRole(Role.STAFF);
        testUser.setActive(true);
        testUser.setEmailVerified(true);

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("new@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("New");
        registerRequest.setLastName("User");
        registerRequest.setRole(Role.STAFF);

        loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");
    }

    @Test
    void testRegisterSuccess() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        AuthResponse response = userService.register(registerRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterWithExistingUsername() {
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterWithExistingEmail() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterStaffRoleBlocked() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> userService.register(registerRequest));
        assertEquals("STAFF role can only be created by an OWNER.", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testLoginSuccess() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail(anyString())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("jwtToken");

        AuthResponse response = userService.login(loginRequest);

        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
    }

    @Test
    void testLoginWithInvalidUsername() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(userRepository.findByUsernameOrEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.login(loginRequest));
    }

    @Test
    void testFindByIdSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    void testFindByIdNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<User> result = userService.findById(1L);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByUsernameSuccess() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    void testFindByUsernameNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("nonexistent");

        assertFalse(result.isPresent());
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        when(userRepository.findByUsernameOrEmail(anyString())).thenReturn(Optional.of(testUser));

        var result = userService.loadUserByUsername("testuser");

        assertNotNull(result);
        assertEquals(testUser.getUsername(), result.getUsername());
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByUsernameOrEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(org.springframework.security.core.userdetails.UsernameNotFoundException.class,
            () -> userService.loadUserByUsername("nonexistent"));
    }
}
