package com.trainersindia.portal.service;

import com.trainersindia.portal.dto.JobApplicationRequest;
import com.trainersindia.portal.dto.JobApplicationResponse;
import com.trainersindia.portal.entity.ApplicationStatus;

import java.util.List;

public interface JobApplicationService {
    JobApplicationResponse apply(JobApplicationRequest request, String username);
    JobApplicationResponse withdraw(Long applicationId, String username);
    JobApplicationResponse updateStatus(Long applicationId, ApplicationStatus status, String username);
    List<JobApplicationResponse> getTrainerApplications(String username);
    List<JobApplicationResponse> getJobApplications(Long postId, String username);
    JobApplicationResponse getApplication(Long applicationId, String username);
} 