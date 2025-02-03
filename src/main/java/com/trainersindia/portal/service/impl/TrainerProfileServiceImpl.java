package com.trainersindia.portal.service.impl;

import com.trainersindia.portal.dto.TrainerProfileRequest;
import com.trainersindia.portal.entity.TrainerProfile;
import com.trainersindia.portal.entity.User;
import com.trainersindia.portal.exception.UserException;
import com.trainersindia.portal.repository.TrainerProfileRepository;
import com.trainersindia.portal.repository.UserRepository;
import com.trainersindia.portal.service.FileStorageService;
import com.trainersindia.portal.service.TrainerProfileService;
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
    public TrainerProfile createProfile(TrainerProfileRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        if (profileRepository.existsByUser(user)) {
            throw new UserException("Profile already exists", HttpStatus.CONFLICT);
        }

        TrainerProfile profile = new TrainerProfile();
        mapRequestToProfile(request, profile);
        profile.setUser(user);
        
        return profileRepository.save(profile);
    }

    @Override
    @Transactional
    public TrainerProfile updateProfile(TrainerProfileRequest request, String username) {
        TrainerProfile profile = getProfileByUsername(username);
        mapRequestToProfile(request, profile);
        return profileRepository.save(profile);
    }

    @Override
    public TrainerProfile getProfile(String username) {
        return getProfileByUsername(username);
    }

    @Override
    @Transactional
    public TrainerProfile uploadProfilePicture(MultipartFile file, String username) {
        TrainerProfile profile = getProfileByUsername(username);
        String fileUrl = fileStorageService.storeFile(file, "profile-pictures");
        
        // Delete old picture if exists
        if (profile.getProfilePictureUrl() != null) {
            fileStorageService.deleteFile(profile.getProfilePictureUrl());
        }
        
        profile.setProfilePictureUrl(fileUrl);
        return profileRepository.save(profile);
    }

    @Override
    @Transactional
    public TrainerProfile uploadResume(MultipartFile file, String username) {
        TrainerProfile profile = getProfileByUsername(username);
        String fileUrl = fileStorageService.storeFile(file, "resumes");
        
        // Delete old resume if exists
        if (profile.getResumeUrl() != null) {
            fileStorageService.deleteFile(profile.getResumeUrl());
        }
        
        profile.setResumeUrl(fileUrl);
        return profileRepository.save(profile);
    }

    @Override
    @Transactional
    public TrainerProfile deleteProfilePicture(String username) {
        TrainerProfile profile = getProfileByUsername(username);
        if (profile.getProfilePictureUrl() != null) {
            fileStorageService.deleteFile(profile.getProfilePictureUrl());
            profile.setProfilePictureUrl(null);
            return profileRepository.save(profile);
        }
        return profile;
    }

    @Override
    @Transactional
    public TrainerProfile deleteResume(String username) {
        TrainerProfile profile = getProfileByUsername(username);
        if (profile.getResumeUrl() != null) {
            fileStorageService.deleteFile(profile.getResumeUrl());
            profile.setResumeUrl(null);
            return profileRepository.save(profile);
        }
        return profile;
    }

    private TrainerProfile getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));
        
        return profileRepository.findByUser(user)
                .orElseThrow(() -> new UserException("Profile not found", HttpStatus.NOT_FOUND));
    }

    private void mapRequestToProfile(TrainerProfileRequest request, TrainerProfile profile) {
        profile.setFullName(request.getFullName());
        profile.setEmail(request.getEmail());
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
    }
} 