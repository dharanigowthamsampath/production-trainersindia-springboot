package com.trainersindia.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class TrainerProfileRequest {
    @Pattern(regexp = "^(\\+91[\\-\\s]?)?[0]?(91)?[6789]\\d{9}$", message = "Invalid phone number format. Use Indian format like: +91-9876543210 or 9876543210")
    private String phoneNumber;

    @NotBlank(message = "Location is required")
    private String location;

    private String bio;

    @NotBlank(message = "Experience is required")
    private String experience;

    @NotEmpty(message = "At least one skill is required")
    private List<String> skills;

    @NotBlank(message = "Education is required")
    private String education;

    private List<String> certifications;

    private String previousTrainings;

    @NotBlank(message = "Availability is required")
    private String availability;

    @NotBlank(message = "Expected salary is required")
    private String expectedSalary;

    @Pattern(regexp = "^https?://.*", message = "Invalid LinkedIn URL")
    private String linkedin;
} 