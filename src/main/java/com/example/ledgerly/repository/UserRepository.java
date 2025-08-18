package com.example.ledgerly.repository;

import com.example.ledgerly.entity.Role;
import com.example.ledgerly.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username or email
     */
    @Query("SELECT u FROM User u WHERE u.username = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find all users by role
     */
    List<User> findByRole(Role role);

    /**
     * Find all active users
     */
    List<User> findByIsActiveTrue();

    /**
     * Find all users by role and active status
     */
    List<User> findByRoleAndIsActiveTrue(Role role);

    /**
     * Find all verified users
     */
    List<User> findByIsEmailVerifiedTrue();

    /**
     * Count users by role
     */
    long countByRole(Role role);

    /**
     * Count active users
     */
    long countByIsActiveTrue();
}
