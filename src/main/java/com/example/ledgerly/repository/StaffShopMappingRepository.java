package com.example.ledgerly.repository;

import com.example.ledgerly.entity.StaffShopMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffShopMappingRepository extends JpaRepository<StaffShopMapping, Long> {
    
    /**
     * Find mapping by staff user ID
     */
    Optional<StaffShopMapping> findByStaffId(Long staffId);
    
    /**
     * Find all staff mappings for a specific shop
     */
    List<StaffShopMapping> findByShopId(Long shopId);
    
    /**
     * Find all active staff mappings for a specific shop
     */
    List<StaffShopMapping> findByShopIdAndIsActiveTrue(Long shopId);
    
    /**
     * Check if staff is already mapped to any shop
     */
    boolean existsByStaffId(Long staffId);
    
    /**
     * Check if staff is mapped to a specific shop
     */
    boolean existsByStaffIdAndShopId(Long staffId, Long shopId);
    
    /**
     * Find all active staff mappings
     */
    List<StaffShopMapping> findByIsActiveTrue();
    
    /**
     * Find mapping by staff user ID and shop ID
     */
    Optional<StaffShopMapping> findByStaffIdAndShopId(Long staffId, Long shopId);
}
