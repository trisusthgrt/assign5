package com.example.ledgerly.repository;

import com.example.ledgerly.entity.Customer;
import com.example.ledgerly.entity.RelationshipType;
import com.example.ledgerly.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer entity operations
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find customers by name containing (case-insensitive search)
     */
    List<Customer> findByNameContainingIgnoreCase(String name);

    /**
     * Find customers by email
     */
    Optional<Customer> findByEmailIgnoreCase(String email);

    /**
     * Find customers by phone number
     */
    Optional<Customer> findByPhoneNumber(String phoneNumber);

    /**
     * Find customers by relationship type
     */
    List<Customer> findByRelationshipType(RelationshipType relationshipType);

    /**
     * Find customers by relationship type and active status
     */
    List<Customer> findByRelationshipTypeAndIsActive(RelationshipType relationshipType, boolean isActive);

    /**
     * Find active customers only
     */
    List<Customer> findByIsActiveTrue();

    /**
     * Find customers created by a specific user
     */
    List<Customer> findByCreatedBy(User createdBy);

    /**
     * Find customers created by a specific user with pagination
     */
    Page<Customer> findByCreatedBy(User createdBy, Pageable pageable);

    /**
     * Find customers by business name containing (case-insensitive search)
     */
    List<Customer> findByBusinessNameContainingIgnoreCase(String businessName);

    /**
     * Search customers by multiple criteria
     */
    @Query("SELECT c FROM Customer c WHERE " +
           "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
           "(:phoneNumber IS NULL OR c.phoneNumber LIKE CONCAT('%', :phoneNumber, '%')) AND " +
           "(:relationshipType IS NULL OR c.relationshipType = :relationshipType) AND " +
           "(:isActive IS NULL OR c.isActive = :isActive) AND " +
           "(:businessName IS NULL OR LOWER(c.businessName) LIKE LOWER(CONCAT('%', :businessName, '%')))")
    Page<Customer> searchCustomers(@Param("name") String name,
                                  @Param("email") String email,
                                  @Param("phoneNumber") String phoneNumber,
                                  @Param("relationshipType") RelationshipType relationshipType,
                                  @Param("isActive") Boolean isActive,
                                  @Param("businessName") String businessName,
                                  Pageable pageable);

    /**
     * Count customers by relationship type
     */
    long countByRelationshipType(RelationshipType relationshipType);

    /**
     * Count active customers
     */
    long countByIsActiveTrue();

    /**
     * Check if customer exists by email (excluding current customer for updates)
     */
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE LOWER(c.email) = LOWER(:email) AND c.id != :excludeId")
    boolean existsByEmailIgnoreCaseAndIdNot(@Param("email") String email, @Param("excludeId") Long excludeId);

    /**
     * Check if customer exists by phone number (excluding current customer for updates)
     */
    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.phoneNumber = :phoneNumber AND c.id != :excludeId")
    boolean existsByPhoneNumberAndIdNot(@Param("phoneNumber") String phoneNumber, @Param("excludeId") Long excludeId);

    /**
     * Find customers with notes containing specific text
     */
    @Query("SELECT c FROM Customer c WHERE c.notes IS NOT NULL AND LOWER(c.notes) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Customer> findByNotesContaining(@Param("searchText") String searchText);

    /**
     * Find customers with outstanding balance
     */
    @Query("SELECT c FROM Customer c WHERE c.currentBalance != 0")
    List<Customer> findCustomersWithOutstandingBalance();

    /**
     * Find customers exceeding credit limit
     */
    @Query("SELECT c FROM Customer c WHERE c.currentBalance > c.creditLimit AND c.creditLimit > 0")
    List<Customer> findCustomersExceedingCreditLimit();
}
