package com.trainersindia.portal.service;

import com.trainersindia.portal.dto.RegisterRequest;
import com.trainersindia.portal.dto.VerificationRequest;
import com.trainersindia.portal.entity.EmailVerificationToken;
import com.trainersindia.portal.entity.User;
import com.trainersindia.portal.repository.EmailVerificationTokenRepository;
import com.trainersindia.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public String initiateRegistration(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // Create unverified user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRoles(Collections.singleton(request.getRole().name()));
        user.setActive(false); // User is inactive until email is verified

        // Generate verification code
        String verificationCode = generateVerificationCode();
        
        // Create verification token
        EmailVerificationToken token = new EmailVerificationToken();
        token.setEmail(request.getEmail());
        token.setCode(verificationCode);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(15)); // 15 minutes validity
        token.setUser(user);
        
        tokenRepository.save(token);

        // Send verification email
        emailService.sendVerificationEmail(request.getEmail(), verificationCode);

        return "Verification code sent to " + request.getEmail();
    }

    @Transactional
    public User verifyAndRegister(VerificationRequest request) {
        EmailVerificationToken token = tokenRepository.findByEmailAndCodeAndUsedFalse(request.getEmail(), request.getCode())
                .orElseThrow(() -> new RuntimeException("Invalid verification code"));

        if (token.isExpired()) {
            throw new RuntimeException("Verification code has expired");
        }

        User user = token.getUser();
        user.setActive(true);
        token.setUsed(true);

        tokenRepository.save(token);
        return userRepository.save(user);
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6-digit code
        return String.valueOf(code);
    }
} 