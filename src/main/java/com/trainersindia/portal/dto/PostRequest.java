package com.trainersindia.portal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class PostRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Job type is required")
    private String jobType;

    @NotBlank(message = "Experience level is required")
    private String experienceLevel;

    @NotBlank(message = "Salary range is required")
    private String salaryRange;

    @NotBlank(message = "Description is required")
    private String description;

    @NotEmpty(message = "Skills are required")
    private List<String> skillsRequired;

    @NotBlank(message = "Qualifications are required")
    private String qualifications;

    @NotNull(message = "Application deadline is required")
    private LocalDate applicationDeadline;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Contact email is required")
    private String contactEmail;
} 