package com.trainersindia.portal.controller;

import com.trainersindia.portal.dto.LoginRequest;
import com.trainersindia.portal.dto.LoginResponse;
import com.trainersindia.portal.dto.PasswordResetRequest;
import com.trainersindia.portal.dto.PasswordResetConfirmRequest;
import com.trainersindia.portal.dto.RegisterRequest;
import com.trainersindia.portal.dto.VerificationRequest;
import com.trainersindia.portal.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication Controller for handling user registration, login, and password reset
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Initiate user registration
     * 
     * @RequestBody:
     * {
     *   "username": "string",
     *   "email": "user@example.com",
     *   "password": "string (min 6 chars)",
     *   "fullName": "string",
     *   "role": "ROLE_ADMIN|ROLE_COMPANY|ROLE_TRAINER"
     * }
     * 
     * @Response:
     * Success (200): "Verification code sent to email@example.com"
     * Error (400): {
     *   "timestamp": "2024-02-02T12:00:00",
     *   "status": "BAD_REQUEST",
     *   "errors": {
     *     "username": "Username is already taken",
     *     "email": "Email is already registered"
     *   }
     * }
     */
    @PostMapping("/register/initiate")
    public ResponseEntity<?> initiateRegistration(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            String response = authService.initiateRegistration(registerRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Verify email and complete registration
     * 
     * @RequestBody:
     * {
     *   "email": "user@example.com",
     *   "code": "123456"
     * }
     * 
     * @Response:
     * Success (200): "User registered successfully"
     * Error (400): {
     *   "timestamp": "2024-02-02T12:00:00",
     *   "status": "BAD_REQUEST",
     *   "message": "Invalid verification code"
     * }
     */
    @PostMapping("/register/verify")
    public ResponseEntity<?> verifyAndRegister(@Valid @RequestBody VerificationRequest verificationRequest) {
        try {
            authService.verifyAndRegister(verificationRequest);
            return ResponseEntity.ok("User registered successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * User login
     * 
     * @RequestBody:
     * {
     *   "username": "string",
     *   "password": "string"
     * }
     * 
     * @Response:
     * Success (200): {
     *   "token": "JWT_TOKEN",
     *   "type": "Bearer",
     *   "username": "string",
     *   "email": "user@example.com",
     *   "fullName": "string",
     *   "role": "ROLE_ADMIN|ROLE_COMPANY|ROLE_TRAINER"
     * }
     * Error (401): {
     *   "timestamp": "2024-02-02T12:00:00",
     *   "status": "UNAUTHORIZED",
     *   "message": "Invalid username or password"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }
    }

    /**
     * Initiate password reset
     * 
     * @RequestBody:
     * {
     *   "email": "user@example.com"
     * }
     * 
     * @Response:
     * Success (200): "Password reset code sent to user@example.com"
     * Error (400): {
     *   "timestamp": "2024-02-02T12:00:00",
     *   "status": "BAD_REQUEST",
     *   "message": "User not found with this email"
     * }
     */
    @PostMapping("/password/reset/initiate")
    public ResponseEntity<?> initiatePasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        try {
            String response = authService.initiatePasswordReset(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Complete password reset
     * 
     * @RequestBody:
     * {
     *   "email": "user@example.com",
     *   "code": "123456",
     *   "newPassword": "string (min 6 chars)"
     * }
     * 
     * @Response:
     * Success (200): "Password reset successful"
     * Error (400): {
     *   "timestamp": "2024-02-02T12:00:00",
     *   "status": "BAD_REQUEST",
     *   "message": "Invalid reset code"
     * }
     */
    @PostMapping("/password/reset/confirm")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetConfirmRequest request) {
        try {
            String response = authService.resetPassword(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 