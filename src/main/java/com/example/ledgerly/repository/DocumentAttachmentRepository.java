package com.example.ledgerly.repository;

import com.example.ledgerly.entity.DocumentAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DocumentAttachment entity operations
 */
@Repository
public interface DocumentAttachmentRepository extends JpaRepository<DocumentAttachment, Long> {

    /**
     * Find attachments by ledger entry ID
     */
    @Query("SELECT da FROM DocumentAttachment da WHERE da.id IN (SELECT att.id FROM LedgerEntry le JOIN le.attachments att WHERE le.id = :ledgerEntryId) AND da.isActive = true")
    List<DocumentAttachment> findByLedgerEntryId(@Param("ledgerEntryId") Long ledgerEntryId);

    /**
     * Find attachment by file name
     */
    Optional<DocumentAttachment> findByFileNameAndIsActiveTrue(String fileName);

    /**
     * Find attachments by original file name
     */
    List<DocumentAttachment> findByOriginalFileNameContainingIgnoreCaseAndIsActiveTrue(String originalFileName);

    /**
     * Find attachments by content type
     */
    List<DocumentAttachment> findByContentTypeAndIsActiveTrue(String contentType);

    /**
     * Find image attachments
     */
    @Query("SELECT da FROM DocumentAttachment da WHERE da.contentType LIKE 'image/%' AND da.isActive = true ORDER BY da.uploadedAt DESC")
    List<DocumentAttachment> findImageAttachments();

    /**
     * Find PDF attachments
     */
    List<DocumentAttachment> findByContentTypeAndIsActiveTrueOrderByUploadedAtDesc(String contentType);

    /**
     * Find attachments by uploaded user
     */
    @Query("SELECT da FROM DocumentAttachment da WHERE da.uploadedBy.id = :userId AND da.isActive = true ORDER BY da.uploadedAt DESC")
    List<DocumentAttachment> findByUploadedBy(@Param("userId") Long userId);

    /**
     * Count total attachments
     */
    long countByIsActiveTrue();

    /**
     * Count attachments by content type
     */
    long countByContentTypeAndIsActiveTrue(String contentType);

    /**
     * Find large files (above certain size)
     */
    @Query("SELECT da FROM DocumentAttachment da WHERE da.fileSize > :minSize AND da.isActive = true ORDER BY da.fileSize DESC")
    List<DocumentAttachment> findLargeFiles(@Param("minSize") Long minSize);

    /**
     * Calculate total storage used
     */
    @Query("SELECT COALESCE(SUM(da.fileSize), 0) FROM DocumentAttachment da WHERE da.isActive = true")
    Long calculateTotalStorageUsed();

    /**
     * Find orphaned attachments (not linked to any ledger entry)
     */
    @Query("SELECT da FROM DocumentAttachment da WHERE da.isActive = true AND da.id NOT IN (SELECT att.id FROM LedgerEntry le JOIN le.attachments att)")
    List<DocumentAttachment> findOrphanedAttachments();
}
