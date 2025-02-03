package com.trainersindia.portal.service;

import com.trainersindia.portal.dto.TrainerProfileRequest;
import com.trainersindia.portal.entity.TrainerProfile;
import org.springframework.web.multipart.MultipartFile;

public interface TrainerProfileService {
    TrainerProfile createProfile(TrainerProfileRequest request, String username);
    TrainerProfile updateProfile(TrainerProfileRequest request, String username);
    TrainerProfile getProfile(String username);
    TrainerProfile uploadProfilePicture(MultipartFile file, String username);
    TrainerProfile uploadResume(MultipartFile file, String username);
    TrainerProfile deleteProfilePicture(String username);
    TrainerProfile deleteResume(String username);
} 