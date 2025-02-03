package com.trainersindia.portal.service;

import com.trainersindia.portal.dto.TrainerProfileRequest;
import com.trainersindia.portal.dto.TrainerProfileResponse;
import org.springframework.web.multipart.MultipartFile;

public interface TrainerProfileService {
    TrainerProfileResponse createProfile(TrainerProfileRequest request, String username);
    TrainerProfileResponse updateProfile(TrainerProfileRequest request, String username);
    TrainerProfileResponse getProfile(String username);
    TrainerProfileResponse uploadProfilePicture(MultipartFile file, String username);
    TrainerProfileResponse uploadResume(MultipartFile file, String username);
    TrainerProfileResponse deleteProfilePicture(String username);
    TrainerProfileResponse deleteResume(String username);
} 