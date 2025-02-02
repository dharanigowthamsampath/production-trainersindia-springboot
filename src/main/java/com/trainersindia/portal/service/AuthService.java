package com.trainersindia.portal.service;

import com.trainersindia.portal.dto.LoginRequest;
import com.trainersindia.portal.dto.LoginResponse;
import com.trainersindia.portal.dto.RegisterRequest;
import com.trainersindia.portal.dto.VerificationRequest;
import com.trainersindia.portal.dto.PasswordResetRequest;
import com.trainersindia.portal.dto.PasswordResetConfirmRequest;
import com.trainersindia.portal.entity.EmailVerificationToken;
import com.trainersindia.portal.entity.User;
import com.trainersindia.portal.repository.EmailVerificationTokenRepository;
import com.trainersindia.portal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.trainersindia.portal.security.JwtTokenProvider;
import com.trainersindia.portal.security.UserPrincipal;

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
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

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

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        
        return LoginResponse.builder()
                .token(jwt)
                .type("Bearer")
                .username(userPrincipal.getUsername())
                .email(userPrincipal.getEmail())
                .fullName(userPrincipal.getFullName())
                .role(userPrincipal.getAuthorities().iterator().next().getAuthority())
                .build();
    }

    public String initiatePasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email"));

        String resetCode = generateVerificationCode();
        
        // Find existing token or create new one
        EmailVerificationToken token = tokenRepository.findByUser(user)
                .map(existingToken -> {
                    existingToken.setCode(resetCode);
                    existingToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
                    existingToken.setUsed(false);
                    return existingToken;
                })
                .orElseGet(() -> {
                    EmailVerificationToken newToken = new EmailVerificationToken();
                    newToken.setEmail(request.getEmail());
                    newToken.setCode(resetCode);
                    newToken.setExpiryDate(LocalDateTime.now().plusMinutes(15));
                    newToken.setUser(user);
                    return newToken;
                });
        
        tokenRepository.save(token);
        emailService.sendPasswordResetEmail(request.getEmail(), resetCode);

        return "Password reset code sent to " + request.getEmail();
    }

    @Transactional
    public String resetPassword(PasswordResetConfirmRequest request) {
        EmailVerificationToken token = tokenRepository.findByEmailAndCodeAndUsedFalse(
                request.getEmail(), request.getCode())
                .orElseThrow(() -> new RuntimeException("Invalid reset code"));

        if (token.isExpired()) {
            throw new RuntimeException("Reset code has expired");
        }

        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        token.setUsed(true);

        tokenRepository.save(token);
        userRepository.save(user);

        return "Password reset successful";
    }
} 