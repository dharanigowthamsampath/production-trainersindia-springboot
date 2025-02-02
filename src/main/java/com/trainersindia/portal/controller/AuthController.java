package com.trainersindia.portal.controller;

import com.trainersindia.portal.dto.RegisterRequest;
import com.trainersindia.portal.dto.VerificationRequest;
import com.trainersindia.portal.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
} 