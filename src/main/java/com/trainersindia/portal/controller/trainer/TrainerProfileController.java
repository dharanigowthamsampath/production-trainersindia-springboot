package com.trainersindia.portal.controller.trainer;

import com.trainersindia.portal.dto.TrainerProfileRequest;
import com.trainersindia.portal.dto.TrainerProfileResponse;
import com.trainersindia.portal.security.UserPrincipal;
import com.trainersindia.portal.service.TrainerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/trainer/profile")
@PreAuthorize("hasRole('TRAINER')")
@RequiredArgsConstructor
public class TrainerProfileController {

    private final TrainerProfileService profileService;

    @PostMapping
    public ResponseEntity<TrainerProfileResponse> createProfile(
            @Valid @RequestBody TrainerProfileRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TrainerProfileResponse profile = profileService.createProfile(request, userPrincipal.getUsername());
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<TrainerProfileResponse> updateProfile(
            @Valid @RequestBody TrainerProfileRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TrainerProfileResponse profile = profileService.updateProfile(request, userPrincipal.getUsername());
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    public ResponseEntity<TrainerProfileResponse> getProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TrainerProfileResponse profile = profileService.getProfile(userPrincipal.getUsername());
        return ResponseEntity.ok(profile);
    }

    @PostMapping(value = "/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrainerProfileResponse> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TrainerProfileResponse profile = profileService.uploadProfilePicture(file, userPrincipal.getUsername());
        return ResponseEntity.ok(profile);
    }

    @PostMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrainerProfileResponse> uploadResume(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TrainerProfileResponse profile = profileService.uploadResume(file, userPrincipal.getUsername());
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/picture")
    public ResponseEntity<TrainerProfileResponse> deleteProfilePicture(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TrainerProfileResponse profile = profileService.deleteProfilePicture(userPrincipal.getUsername());
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/resume")
    public ResponseEntity<TrainerProfileResponse> deleteResume(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TrainerProfileResponse profile = profileService.deleteResume(userPrincipal.getUsername());
        return ResponseEntity.ok(profile);
    }
} 