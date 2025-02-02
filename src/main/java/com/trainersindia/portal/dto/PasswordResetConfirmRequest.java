package com.trainersindia.portal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetConfirmRequest {
    @NotBlank(message = "Email is required")
    private String email;
    
    @NotBlank(message = "Reset code is required")
    private String code;
    
    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
} 