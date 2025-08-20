package com.example.ledgerly.controller;

import com.example.ledgerly.entity.DocumentAttachment;
import com.example.ledgerly.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
@PreAuthorize("hasRole('ADMIN') or hasRole('OWNER') or hasRole('STAFF')")
public class FileController {

    private final FileUploadService fileUploadService;

    @Autowired
    public FileController(FileUploadService fileUploadService) {
        this.fileUploadService = fileUploadService;
    }

    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long attachmentId) {
        try {
            DocumentAttachment attachment = fileUploadService.getAttachmentById(attachmentId);
            Resource resource = fileUploadService.downloadFileById(attachmentId);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(attachment.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + attachment.getOriginalFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> getFileStorageStats() {
        try {
            FileUploadService.FileStorageStats stats = fileUploadService.getStorageStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", Map.of(
                    "totalFiles", stats.getTotalFiles(),
                    "totalSizeBytes", stats.getTotalSizeBytes(),
                    "formattedTotalSize", stats.getFormattedTotalSize(),
                    "imageFiles", stats.getImageFiles(),
                    "documentFiles", stats.getDocumentFiles()
            ));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch file storage stats: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
