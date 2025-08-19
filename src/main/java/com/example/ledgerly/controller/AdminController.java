package com.example.ledgerly.controller;

import com.example.ledgerly.dto.BasicUserResponse;
import com.example.ledgerly.dto.UserCreateRequest;
import com.example.ledgerly.dto.UserUpdateRequest;
import com.example.ledgerly.entity.Role;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/owners")
    public ResponseEntity<BasicUserResponse> createOwner(@Valid @RequestBody UserCreateRequest request) {
        if (request.getRole() != Role.OWNER) {
            return ResponseEntity.badRequest().build();
        }
        if (userRepository.existsByUsername(request.getUsername()) || userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(409).build();
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.OWNER);
        user.setEmailVerified(true);
        user.setActive(true);
        User saved = userRepository.save(user);
        return ResponseEntity.created(URI.create("/api/v1/admin/users/" + saved.getId())).body(toDto(saved));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/owners/{id}")
    public ResponseEntity<BasicUserResponse> updateOwner(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return userRepository.findById(id)
                .filter(u -> u.getRole() == Role.OWNER)
                .map(u -> {
                    u.setFirstName(request.getFirstName());
                    u.setLastName(request.getLastName());
                    if (request.getEmail() != null) u.setEmail(request.getEmail());
                    if (request.getPhoneNumber() != null) u.setPhoneNumber(request.getPhoneNumber());
                    return ResponseEntity.ok(toDto(userRepository.save(u)));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/owners/{id}")
    public ResponseEntity<?> deleteOwner(@PathVariable Long id) {
        return userRepository.findById(id)
                .filter(u -> u.getRole() == Role.OWNER)
                .map(u -> {
                    userRepository.delete(u);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/owners")
    public ResponseEntity<List<BasicUserResponse>> listOwners() {
        List<BasicUserResponse> owners = userRepository.findByRole(Role.OWNER).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(owners);
    }

    private BasicUserResponse toDto(User u) {
        BasicUserResponse dto = new BasicUserResponse();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setFirstName(u.getFirstName());
        dto.setLastName(u.getLastName());
        dto.setRole(u.getRole());
        dto.setActive(u.isActive());
        return dto;
    }
}


