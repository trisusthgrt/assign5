package com.example.ledgerly.repository;

import com.example.ledgerly.entity.Shop;
import com.example.ledgerly.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    
    /**
     * Find all shops owned by a specific user
     */
    List<Shop> findByOwnerId(Long ownerId);
    
    /**
     * Find all shops owned by a specific user that are active
     */
    List<Shop> findByOwnerIdAndIsActiveTrue(Long ownerId);
    
    /**
     * Find shop by name and owner (for uniqueness validation)
     */
    Optional<Shop> findByNameAndOwnerId(String name, Long ownerId);
    
    /**
     * Check if shop name exists for a specific owner
     */
    boolean existsByNameAndOwnerId(String name, Long ownerId);
    
    /**
     * Find all active shops
     */
    List<Shop> findByIsActiveTrue();
    
    /**
     * Find shops by city
     */
    List<Shop> findByCityAndIsActiveTrue(String city);
    
    /**
     * Find shops by state
     */
    List<Shop> findByStateAndIsActiveTrue(String state);
    
    /**
     * Find shops by GST number
     */
    Optional<Shop> findByGstNumber(String gstNumber);
    
    /**
     * Find shops by PAN number
     */
    Optional<Shop> findByPanNumber(String panNumber);
}
