package com.trainersindia.portal.dto;

import com.trainersindia.portal.entity.TrainerProfile;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TrainerProfileResponse {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String location;
    private String profilePictureUrl;
    private String bio;
    private String experience;
    private List<String> skills;
    private String education;
    private List<String> certifications;
    private String previousTrainings;
    private String availability;
    private String expectedSalary;
    private String resumeUrl;
    private String linkedinUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TrainerProfileResponse fromEntity(TrainerProfile profile) {
        TrainerProfileResponse response = new TrainerProfileResponse();
        response.setId(profile.getId());
        response.setUsername(profile.getUser().getUsername());
        response.setFullName(profile.getFullName());
        response.setEmail(profile.getEmail());
        response.setPhoneNumber(profile.getPhoneNumber());
        response.setLocation(profile.getLocation());
        response.setProfilePictureUrl(profile.getProfilePictureUrl());
        response.setBio(profile.getBio());
        response.setExperience(profile.getExperience());
        response.setSkills(profile.getSkills());
        response.setEducation(profile.getEducation());
        response.setCertifications(profile.getCertifications());
        response.setPreviousTrainings(profile.getPreviousTrainings());
        response.setAvailability(profile.getAvailability());
        response.setExpectedSalary(profile.getExpectedSalary());
        response.setResumeUrl(profile.getResumeUrl());
        response.setLinkedinUrl(profile.getLinkedinUrl());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());
        return response;
    }
} 