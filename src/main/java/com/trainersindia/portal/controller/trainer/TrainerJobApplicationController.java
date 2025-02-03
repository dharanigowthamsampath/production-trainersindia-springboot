package com.trainersindia.portal.controller.trainer;

import com.trainersindia.portal.dto.JobApplicationRequest;
import com.trainersindia.portal.dto.JobApplicationResponse;
import com.trainersindia.portal.security.UserPrincipal;
import com.trainersindia.portal.service.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trainer/applications")
@PreAuthorize("hasRole('TRAINER')")
@RequiredArgsConstructor
public class TrainerJobApplicationController {

    private final JobApplicationService applicationService;

    @PostMapping
    public ResponseEntity<JobApplicationResponse> applyForJob(
            @Valid @RequestBody JobApplicationRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        JobApplicationResponse application = applicationService.apply(request, userPrincipal.getUsername());
        return ResponseEntity.ok(application);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<JobApplicationResponse> withdrawApplication(
            @PathVariable("id") Long applicationId,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        JobApplicationResponse application = applicationService.withdraw(applicationId, userPrincipal.getUsername());
        return ResponseEntity.ok(application);
    }

    @GetMapping
    public ResponseEntity<List<JobApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<JobApplicationResponse> applications = applicationService.getTrainerApplications(userPrincipal.getUsername());
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
} 