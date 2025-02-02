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

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/initiate")
    public ResponseEntity<?> initiateRegistration(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            String response = authService.initiateRegistration(registerRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register/verify")
    public ResponseEntity<?> verifyAndRegister(@Valid @RequestBody VerificationRequest verificationRequest) {
        try {
            authService.verifyAndRegister(verificationRequest);
            return ResponseEntity.ok("User registered successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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

    @PostMapping("/password/reset/initiate")
    public ResponseEntity<?> initiatePasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        try {
            String response = authService.initiatePasswordReset(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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