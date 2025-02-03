package com.trainersindia.portal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class TrainerProfileRequest {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Pattern(regexp = "^\\+?[1-9][0-9]{7,14}$", message = "Invalid phone number")
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