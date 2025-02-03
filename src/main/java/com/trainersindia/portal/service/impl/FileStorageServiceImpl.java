package com.trainersindia.portal.service.impl;

import com.trainersindia.portal.config.FileStorageConfig;
import com.trainersindia.portal.entity.FileInfo;
import com.trainersindia.portal.exception.FileStorageException;
import com.trainersindia.portal.repository.FileInfoRepository;
import com.trainersindia.portal.service.FileStorageService;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final FileStorageConfig fileStorageConfig;
    private final FileInfoRepository fileInfoRepository;
    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(fileStorageConfig.getUploadDir())
                .toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (IOException ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String storeFile(@NonNull MultipartFile file, @NonNull String directory) {
        if (directory == null || directory.trim().isEmpty()) {
            throw new FileStorageException("Directory cannot be null or empty", HttpStatus.BAD_REQUEST);
        }
        
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new FileStorageException("Invalid file name", HttpStatus.BAD_REQUEST);
        }
        
        originalFileName = StringUtils.cleanPath(originalFileName);
        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + fileExtension;

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Filename contains invalid path sequence " + fileName, HttpStatus.BAD_REQUEST);
            }

            Path targetLocation = this.fileStorageLocation.resolve(directory).resolve(fileName);
            Files.createDirectories(targetLocation.getParent());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Save file information to database
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileName(fileName);
            fileInfo.setOriginalFileName(originalFileName);
            fileInfo.setFileUrl(getFileUrl(directory + "/" + fileName));
            fileInfo.setFileType(file.getContentType());
            fileInfo.setFileSize(file.getSize());
            fileInfoRepository.save(fileInfo);

            return directory + "/" + fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName, ex, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Path load(String filename) {
        return fileStorageLocation.resolve(filename);
    }

    @Override
    public void deleteFile(String filename) {
        try {
            Path file = load(filename);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new FileStorageException("Error deleting file: " + filename, e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.fileStorageLocation, 1)
                    .filter(path -> !path.equals(this.fileStorageLocation))
                    .map(this.fileStorageLocation::relativize);
        } catch (IOException e) {
            throw new FileStorageException("Failed to read stored files", e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public String getFileUrl(String filename) {
        return fileStorageConfig.getBaseUrl() + "/api/v1/files/" + filename;
    }
} 