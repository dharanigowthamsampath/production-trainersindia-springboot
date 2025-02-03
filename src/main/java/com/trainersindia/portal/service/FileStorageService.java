package com.trainersindia.portal.service;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileStorageService {
    String storeFile(MultipartFile file, String directory);
    Path load(String filename);
    void deleteFile(String filename);
    Stream<Path> loadAll();
    String getFileUrl(String filename);
} 