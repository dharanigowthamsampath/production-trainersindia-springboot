package com.trainersindia.portal.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private String type;
    private String username;
    private String email;
    private String fullName;
    private String role;
} 