package com.trainersindia.portal.controller;

import com.trainersindia.portal.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling file uploads and downloads
 * Requires authentication for all endpoints
 */
@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileStorageService fileStorageService;

    /**
     * Upload a single file
     * 
     * @RequestParam:
     * file: MultipartFile (max size: 200MB)
     * 
     * @Response:
     * Success (200): {
     *   "fileName": "example.pdf",
     *   "fileUrl": "http://localhost:8080/api/v1/files/example.pdf",
     *   "fileType": "application/pdf",
     *   "size": 1048576
     * }
     * Error (400): {
     *   "timestamp": "2024-02-02T12:00:00",
     *   "status": "BAD_REQUEST",
     *   "message": "Failed to upload file"
     * }
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        log.info("Received file upload request for file: {}", file.getOriginalFilename());
        
        String fileName = fileStorageService.storeFile(file);
        String fileUrl = fileStorageService.getFileUrl(fileName);

        Map<String, String> response = new HashMap<>();
        response.put("fileName", fileName);
        response.put("fileUrl", fileUrl);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Upload multiple files
     * 
     * @RequestParam:
     * files: MultipartFile[] (max size per file: 200MB)
     * 
     * @Response:
     * Success (200): [
     *   {
     *     "fileName": "example1.pdf",
     *     "fileUrl": "http://localhost:8080/api/v1/files/example1.pdf",
     *     "fileType": "application/pdf",
     *     "size": 1048576
     *   },
     *   {
     *     "fileName": "example2.jpg",
     *     "fileUrl": "http://localhost:8080/api/v1/files/example2.jpg",
     *     "fileType": "image/jpeg",
     *     "size": 2097152
     *   }
     * ]
     * Error (400): {
     *   "timestamp": "2024-02-02T12:00:00",
     *   "status": "BAD_REQUEST",
     *   "message": "Failed to upload one or more files"
     * }
     */
    @PostMapping("/upload/multiple")
    public ResponseEntity<?> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        // ... implementation ...
        return null; // Placeholder return, actual implementation needed
    }

    /**
     * Download a file by filename
     * 
     * @PathVariable:
     * filename: string
     * 
     * @Response:
     * Success (200): Resource (file download)
     * Error (404): {
     *   "timestamp": "2024-02-02T12:00:00",
     *   "status": "NOT_FOUND",
     *   "message": "File not found"
     * }
     */
    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            Path filePath = fileStorageService.load(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error downloading file: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Delete a file by filename
     * 
     * @PathVariable:
     * filename: string
     * 
     * @Response:
     * Success (200): "File deleted successfully"
     * Error (404): {
     *   "timestamp": "2024-02-02T12:00:00",
     *   "status": "NOT_FOUND",
     *   "message": "File not found"
     * }
     */
    @DeleteMapping("/{filename:.+}")
    public ResponseEntity<Void> deleteFile(@PathVariable String filename) {
        try {
            fileStorageService.deleteFile(filename);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting file: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 