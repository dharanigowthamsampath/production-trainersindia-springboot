package com.trainersindia.portal.controller.company;

import com.trainersindia.portal.dto.JobApplicationResponse;
import com.trainersindia.portal.entity.ApplicationStatus;
import com.trainersindia.portal.security.UserPrincipal;
import com.trainersindia.portal.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/company/applications")
@PreAuthorize("hasRole('COMPANY')")
@RequiredArgsConstructor
public class CompanyJobApplicationController {

    private final JobApplicationService applicationService;

    @GetMapping("/posts/{postId}")
    public ResponseEntity<List<JobApplicationResponse>> getApplicationsForPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<JobApplicationResponse> applications = applicationService.getJobApplications(postId, userPrincipal.getUsername());
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobApplicationResponse> getApplication(
            @PathVariable("id") Long applicationId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        JobApplicationResponse application = applicationService.getApplication(applicationId, userPrincipal.getUsername());
        return ResponseEntity.ok(application);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<JobApplicationResponse> updateApplicationStatus(
            @PathVariable("id") Long applicationId,
            @RequestParam ApplicationStatus status,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        JobApplicationResponse application = applicationService.updateStatus(applicationId, status, userPrincipal.getUsername());
        return ResponseEntity.ok(application);
    }
} 