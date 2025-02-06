package com.trainersindia.portal.controller;

import com.trainersindia.portal.dto.*;
import com.trainersindia.portal.exception.UserException;
import com.trainersindia.portal.service.AuthService;
import com.trainersindia.portal.service.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication Controller for handling user registration, login, and password reset
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

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
    public ResponseEntity<ApiResponse> initiateRegistration(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            String message = authService.initiateRegistration(registerRequest);
            return ResponseEntity.ok(ApiResponse.success(message));
        } catch (UserException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
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
    public ResponseEntity<ApiResponse> verifyAndRegister(@Valid @RequestBody VerificationRequest verificationRequest) {
        try {
            authService.verifyAndRegister(verificationRequest);
            return ResponseEntity.ok(ApiResponse.success("User registered successfully"));
        } catch (UserException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * User login - Step 1: Get access token
     * 
     * @RequestBody:
     * {
     *   "email": "user@example.com",
     *   "password": "string"
     * }
     * 
     * @Response:
     * Success (200): {
     *   "status": "SUCCESS",
     *   "message": "Login successful",
     *   "data": {
     *     "accessToken": "JWT_TOKEN"
     *   }
     * }
     * Error (401): {
     *   "status": "ERROR",
     *   "message": "Invalid email or password",
     *   "data": null
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid email or password"));
        } catch (Exception e) {
            log.error("Login error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("An error occurred during login. Please try again later."));
        }
    }

    /**
     * User login - Step 2: Verify token and get user details
     * 
     * @Response:
     * Success (200): {
     *   "username": "string",
     *   "email": "user@example.com",
     *   "userType": "ROLE_ADMIN|ROLE_COMPANY|ROLE_TRAINER",
     *   "refreshToken": "string"
     * }
     * Error (401): {
     *   "error": "Invalid token"
     * }
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyAndGetDetails(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remove "Bearer " prefix
            UserDetailsResponse response = authService.verifyTokenAndGetDetails(token);
            return ResponseEntity.ok(response);
        } catch (UserException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(Map.of("error", e.getMessage()));
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
     * Success (200): {
     *   "status": "SUCCESS",
     *   "message": "Password reset code sent successfully",
     *   "data": null
     * }
     * Error (400): {
     *   "status": "ERROR",
     *   "message": "User not found with this email",
     *   "data": null
     * }
     */
    @PostMapping("/password/reset/initiate")
    public ResponseEntity<ApiResponse> initiatePasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        try {
            authService.initiatePasswordReset(request);
            return ResponseEntity.ok(ApiResponse.success("Password reset code sent successfully"));
        } catch (UserException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Password reset initiation error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to process password reset request. Please try again later."));
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
     * Success (200): {
     *   "status": "SUCCESS",
     *   "message": "Password reset successful",
     *   "data": null
     * }
     * Error (400): {
     *   "status": "ERROR",
     *   "message": "Invalid reset code",
     *   "data": null
     * }
     */
    @PostMapping("/password/reset/confirm")
    public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody PasswordResetConfirmRequest request) {
        try {
            authService.resetPassword(request);
            return ResponseEntity.ok(ApiResponse.success("Password reset successful"));
        } catch (UserException e) {
            return ResponseEntity.status(e.getStatus())
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Password reset error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to reset password. Please try again later."));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse tokenResponse = refreshTokenService.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        refreshTokenService.revokeRefreshToken(request.getRefreshToken());
        return ResponseEntity.ok().build();
    }
} 