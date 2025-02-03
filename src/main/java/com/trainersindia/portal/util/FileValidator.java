package com.trainersindia.portal.util;

import com.trainersindia.portal.exception.FileStorageException;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FileValidator {
    private static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(
            Arrays.asList("image/jpeg", "image/png", "image/webp")
    );

    private static final Set<String> ALLOWED_RESUME_TYPES = new HashSet<>(
            Arrays.asList("application/pdf")
    );

    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_RESUME_SIZE = 10 * 1024 * 1024; // 10MB

    public static void validateProfilePicture(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file", HttpStatus.BAD_REQUEST);
        }

        if (!ALLOWED_IMAGE_TYPES.contains(file.getContentType())) {
            throw new FileStorageException(
                "Invalid file type. Allowed types are: JPG, JPEG, PNG, WEBP",
                HttpStatus.BAD_REQUEST
            );
        }

        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new FileStorageException(
                "File size exceeds maximum limit of 5MB",
                HttpStatus.BAD_REQUEST
            );
        }
    }

    public static void validateResume(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("Failed to store empty file", HttpStatus.BAD_REQUEST);
        }

        if (!ALLOWED_RESUME_TYPES.contains(file.getContentType())) {
            throw new FileStorageException(
                "Invalid file type. Only PDF files are allowed",
                HttpStatus.BAD_REQUEST
            );
        }

        if (file.getSize() > MAX_RESUME_SIZE) {
            throw new FileStorageException(
                "File size exceeds maximum limit of 10MB",
                HttpStatus.BAD_REQUEST
            );
        }
    }
} 