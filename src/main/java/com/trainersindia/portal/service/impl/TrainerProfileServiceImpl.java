package com.trainersindia.portal.service.impl;

import com.trainersindia.portal.dto.TrainerProfileRequest;
import com.trainersindia.portal.dto.TrainerProfileResponse;
import com.trainersindia.portal.entity.TrainerProfile;
import com.trainersindia.portal.entity.User;
import com.trainersindia.portal.exception.UserException;
import com.trainersindia.portal.repository.TrainerProfileRepository;
import com.trainersindia.portal.repository.UserRepository;
import com.trainersindia.portal.service.FileStorageService;
import com.trainersindia.portal.service.TrainerProfileService;
import com.trainersindia.portal.util.FileValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class TrainerProfileServiceImpl implements TrainerProfileService {

    private final TrainerProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public TrainerProfileResponse createProfile(TrainerProfileRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        if (profileRepository.existsByUser(user)) {
            throw new UserException("Profile already exists", HttpStatus.CONFLICT);
        }

        TrainerProfile profile = new TrainerProfile();
        profile.setUser(user);
        profile.setFullName(user.getFullName());
        profile.setEmail(user.getEmail());
        
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setLocation(request.getLocation());
        profile.setBio(request.getBio());
        profile.setExperience(request.getExperience());
        profile.setSkills(request.getSkills());
        profile.setEducation(request.getEducation());
        profile.setCertifications(request.getCertifications());
        profile.setPreviousTrainings(request.getPreviousTrainings());
        profile.setAvailability(request.getAvailability());
        profile.setExpectedSalary(request.getExpectedSalary());
        profile.setLinkedinUrl(request.getLinkedin());
        
        return TrainerProfileResponse.fromEntity(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public TrainerProfileResponse updateProfile(TrainerProfileRequest request, String username) {
        TrainerProfile profile = getProfileByUsername(username);
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setLocation(request.getLocation());
        profile.setBio(request.getBio());
        profile.setExperience(request.getExperience());
        profile.setSkills(request.getSkills());
        profile.setEducation(request.getEducation());
        profile.setCertifications(request.getCertifications());
        profile.setPreviousTrainings(request.getPreviousTrainings());
        profile.setAvailability(request.getAvailability());
        profile.setExpectedSalary(request.getExpectedSalary());
        profile.setLinkedinUrl(request.getLinkedin());
        return TrainerProfileResponse.fromEntity(profileRepository.save(profile));
    }

    @Override
    public TrainerProfileResponse getProfile(String username) {
        return TrainerProfileResponse.fromEntity(getProfileByUsername(username));
    }

    @Override
    @Transactional
    public TrainerProfileResponse uploadProfilePicture(MultipartFile file, String username) {
        FileValidator.validateProfilePicture(file);
        
        TrainerProfile profile = getProfileByUsername(username);
        String fileUrl = fileStorageService.storeFile(file, "profile-pictures");
        
        if (profile.getProfilePictureUrl() != null) {
            fileStorageService.deleteFile(profile.getProfilePictureUrl());
        }
        
        profile.setProfilePictureUrl(fileUrl);
        return TrainerProfileResponse.fromEntity(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public TrainerProfileResponse uploadResume(MultipartFile file, String username) {
        FileValidator.validateResume(file);
        
        TrainerProfile profile = getProfileByUsername(username);
        String fileUrl = fileStorageService.storeFile(file, "resumes");
        
        if (profile.getResumeUrl() != null) {
            fileStorageService.deleteFile(profile.getResumeUrl());
        }
        
        profile.setResumeUrl(fileUrl);
        return TrainerProfileResponse.fromEntity(profileRepository.save(profile));
    }

    @Override
    @Transactional
    public TrainerProfileResponse deleteProfilePicture(String username) {
        TrainerProfile profile = getProfileByUsername(username);
        if (profile.getProfilePictureUrl() != null) {
            fileStorageService.deleteFile(profile.getProfilePictureUrl());
            profile.setProfilePictureUrl(null);
            return TrainerProfileResponse.fromEntity(profileRepository.save(profile));
        }
        return TrainerProfileResponse.fromEntity(profile);
    }

    @Override
    @Transactional
    public TrainerProfileResponse deleteResume(String username) {
        TrainerProfile profile = getProfileByUsername(username);
        if (profile.getResumeUrl() != null) {
            fileStorageService.deleteFile(profile.getResumeUrl());
            profile.setResumeUrl(null);
            return TrainerProfileResponse.fromEntity(profileRepository.save(profile));
        }
        return TrainerProfileResponse.fromEntity(profile);
    }

    private TrainerProfile getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));
        
        return profileRepository.findByUser(user)
                .orElseThrow(() -> new UserException("Profile not found", HttpStatus.NOT_FOUND));
    }
} 