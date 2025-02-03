package com.trainersindia.portal.dto;

import com.trainersindia.portal.entity.JobApplication;
import com.trainersindia.portal.entity.ApplicationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobApplicationResponse {
    private Long id;
    private Long postId;
    private String postTitle;
    private String companyName;
    private String trainerName;
    private String coverLetter;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static JobApplicationResponse fromEntity(JobApplication application) {
        JobApplicationResponse response = new JobApplicationResponse();
        response.setId(application.getId());
        response.setPostId(application.getPost().getId());
        response.setPostTitle(application.getPost().getTitle());
        response.setCompanyName(application.getPost().getCompanyName());
        response.setTrainerName(application.getTrainer().getFullName());
        response.setCoverLetter(application.getCoverLetter());
        response.setStatus(application.getStatus());
        response.setCreatedAt(application.getCreatedAt());
        response.setUpdatedAt(application.getUpdatedAt());
        return response;
    }
} 