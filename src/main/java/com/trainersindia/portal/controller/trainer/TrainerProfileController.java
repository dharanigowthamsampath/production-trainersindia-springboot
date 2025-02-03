package com.trainersindia.portal.controller.trainer;

import com.trainersindia.portal.dto.TrainerProfileRequest;
import com.trainersindia.portal.entity.TrainerProfile;
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
    public ResponseEntity<TrainerProfile> createProfile(
            @Valid @RequestBody TrainerProfileRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TrainerProfile profile = profileService.createProfile(request, userPrincipal.getUsername());
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<TrainerProfile> updateProfile(
            @Valid @RequestBody TrainerProfileRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TrainerProfile profile = profileService.updateProfile(request, userPrincipal.getUsername());
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    public ResponseEntity<TrainerProfile> getProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TrainerProfile profile = profileService.getProfile(userPrincipal.getUsername());
        return ResponseEntity.ok(profile);
    }

    @PostMapping(value = "/picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrainerProfile> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TrainerProfile profile = profileService.uploadProfilePicture(file, userPrincipal.getUsername());
        return ResponseEntity.ok(profile);
    }

    @PostMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrainerProfile> uploadResume(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TrainerProfile profile = profileService.uploadResume(file, userPrincipal.getUsername());
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/picture")
    public ResponseEntity<TrainerProfile> deleteProfilePicture(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TrainerProfile profile = profileService.deleteProfilePicture(userPrincipal.getUsername());
        return ResponseEntity.ok(profile);
    }

    @DeleteMapping("/resume")
    public ResponseEntity<TrainerProfile> deleteResume(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        TrainerProfile profile = profileService.deleteResume(userPrincipal.getUsername());
        return ResponseEntity.ok(profile);
    }
} 