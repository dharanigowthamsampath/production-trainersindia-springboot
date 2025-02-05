package com.trainersindia.portal.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetailsResponse {
    private String username;
    private String email;
    private String userType;
    private String refreshToken;
} 