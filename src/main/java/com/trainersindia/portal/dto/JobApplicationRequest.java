package com.trainersindia.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobApplicationRequest {
    @NotNull(message = "Post ID is required")
    private Long postId;

    @NotBlank(message = "Cover letter is required")
    private String coverLetter;
} 