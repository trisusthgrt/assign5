package com.example.ledgerly.service;

import com.example.ledgerly.entity.DocumentAttachment;
import com.example.ledgerly.entity.User;
import com.example.ledgerly.repository.DocumentAttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Service for handling file uploads and downloads
 */
@Service
public class FileUploadService {

    private final DocumentAttachmentRepository documentAttachmentRepository;
    private final Path fileStorageLocation;

    // Allowed file types
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp", "image/webp"
    );
    
    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList(
            "application/pdf", "text/plain", "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    );

    // Maximum file size (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    @Autowired
    public FileUploadService(DocumentAttachmentRepository documentAttachmentRepository,
                           @Value("${app.file.upload-dir:uploads}") String uploadDir) {
        this.documentAttachmentRepository = documentAttachmentRepository;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    /**
     * Upload a file and save attachment record
     */
    public DocumentAttachment uploadFile(MultipartFile file, String description, User uploadedBy) {
        validateFile(file);

        // Generate unique filename
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFileName);
        String uniqueFileName = generateUniqueFileName(fileExtension);

        try {
            // Check for invalid characters
            if (originalFileName.contains("..")) {
                throw new RuntimeException("Filename contains invalid path sequence: " + originalFileName);
            }

            // Copy file to the target location
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Create and save attachment record
            DocumentAttachment attachment = new DocumentAttachment();
            attachment.setFileName(uniqueFileName);
            attachment.setOriginalFileName(originalFileName);
            attachment.setFilePath(targetLocation.toString());
            attachment.setContentType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setDescription(description);
            attachment.setUploadedBy(uploadedBy);

            return documentAttachmentRepository.save(attachment);

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    /**
     * Upload multiple files
     */
    public List<DocumentAttachment> uploadMultipleFiles(List<MultipartFile> files, String description, User uploadedBy) {
        return files.stream()
                .map(file -> uploadFile(file, description, uploadedBy))
                .toList();
    }

    /**
     * Download a file
     */
    public Resource downloadFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found: " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found: " + fileName, ex);
        }
    }

    /**
     * Download file by attachment ID
     */
    public Resource downloadFileById(Long attachmentId) {
        DocumentAttachment attachment = documentAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found with id: " + attachmentId));

        if (!attachment.isActive()) {
            throw new RuntimeException("Attachment is not active: " + attachmentId);
        }

        return downloadFile(attachment.getFileName());
    }

    /**
     * Delete a file
     */
    public void deleteFile(String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file: " + fileName, ex);
        }
    }

    /**
     * Delete attachment (soft delete)
     */
    public void deleteAttachment(Long attachmentId) {
        DocumentAttachment attachment = documentAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found with id: " + attachmentId));

        attachment.setActive(false);
        documentAttachmentRepository.save(attachment);
    }

    /**
     * Get attachment by ID
     */
    public DocumentAttachment getAttachmentById(Long attachmentId) {
        return documentAttachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new RuntimeException("Attachment not found with id: " + attachmentId));
    }

    /**
     * Validate uploaded file
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload empty file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File size exceeds maximum allowed size of " + (MAX_FILE_SIZE / 1024 / 1024) + "MB");
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            throw new RuntimeException("File content type is not specified");
        }

        if (!ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase()) && 
            !ALLOWED_DOCUMENT_TYPES.contains(contentType.toLowerCase())) {
            throw new RuntimeException("File type not allowed: " + contentType);
        }
    }

    /**
     * Generate unique filename
     */
    private String generateUniqueFileName(String fileExtension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid + fileExtension;
    }

    /**
     * Get file extension
     */
    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        
        return fileName.substring(lastDotIndex);
    }

    /**
     * Check if file type is image
     */
    public boolean isImageFile(String contentType) {
        return contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase());
    }

    /**
     * Check if file type is document
     */
    public boolean isDocumentFile(String contentType) {
        return contentType != null && ALLOWED_DOCUMENT_TYPES.contains(contentType.toLowerCase());
    }

    /**
     * Get file storage statistics
     */
    public FileStorageStats getStorageStats() {
        long totalFiles = documentAttachmentRepository.countByIsActiveTrue();
        long totalSize = documentAttachmentRepository.calculateTotalStorageUsed();
        long imageFiles = documentAttachmentRepository.countByContentTypeAndIsActiveTrue("image/jpeg") +
                         documentAttachmentRepository.countByContentTypeAndIsActiveTrue("image/png") +
                         documentAttachmentRepository.countByContentTypeAndIsActiveTrue("image/gif");
        long documentFiles = totalFiles - imageFiles;

        return new FileStorageStats(totalFiles, totalSize, imageFiles, documentFiles);
    }

    /**
     * Inner class for storage statistics
     */
    public static class FileStorageStats {
        private final long totalFiles;
        private final long totalSizeBytes;
        private final long imageFiles;
        private final long documentFiles;

        public FileStorageStats(long totalFiles, long totalSizeBytes, long imageFiles, long documentFiles) {
            this.totalFiles = totalFiles;
            this.totalSizeBytes = totalSizeBytes;
            this.imageFiles = imageFiles;
            this.documentFiles = documentFiles;
        }

        public long getTotalFiles() { return totalFiles; }
        public long getTotalSizeBytes() { return totalSizeBytes; }
        public long getImageFiles() { return imageFiles; }
        public long getDocumentFiles() { return documentFiles; }
        
        public String getFormattedTotalSize() {
            if (totalSizeBytes < 1024) {
                return totalSizeBytes + " B";
            } else if (totalSizeBytes < 1024 * 1024) {
                return String.format("%.1f KB", totalSizeBytes / 1024.0);
            } else {
                return String.format("%.1f MB", totalSizeBytes / (1024.0 * 1024.0));
            }
        }
    }
}
