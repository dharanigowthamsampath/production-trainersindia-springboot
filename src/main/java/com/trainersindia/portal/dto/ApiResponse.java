package com.trainersindia.portal.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse {
    private String status;  // "SUCCESS" or "ERROR"
    private String message;
    private Object data;    // Optional data field

    public static ApiResponse success(String message) {
        return ApiResponse.builder()
                .status("SUCCESS")
                .message(message)
                .build();
    }

    public static ApiResponse success(String message, Object data) {
        return ApiResponse.builder()
                .status("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponse error(String message) {
        return ApiResponse.builder()
                .status("ERROR")
                .message(message)
                .build();
    }
} 