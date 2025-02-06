package com.trainersindia.portal.service;

import com.trainersindia.portal.dto.LoginRequest;
import com.trainersindia.portal.dto.LoginResponse;
import com.trainersindia.portal.dto.RegisterRequest;
import com.trainersindia.portal.dto.VerificationRequest;
import com.trainersindia.portal.dto.PasswordResetRequest;
import com.trainersindia.portal.dto.PasswordResetConfirmRequest;
import com.trainersindia.portal.dto.UserDetailsResponse;
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
import com.trainersindia.portal.exception.UserException;
import org.springframework.http.HttpStatus;
import lombok.extern.slf4j.Slf4j;
import com.trainersindia.portal.entity.RefreshToken;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public String initiateRegistration(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserException("Username is already taken", HttpStatus.CONFLICT);
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserException("Email is already registered", HttpStatus.CONFLICT);
        }

        try {
            // Create unverified user
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFullName(request.getFullName());
            user.setRoles(Collections.singleton(request.getRole().name()));
            user.setActive(false); // User is inactive until email is verified
            
            // Save the user first
            user = userRepository.save(user);

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

            log.info("Registration initiated for user: {}", request.getEmail());
            return "Verification code sent to " + request.getEmail();
        } catch (Exception e) {
            log.error("Failed to initiate registration for {}: {}", request.getEmail(), e.getMessage());
            throw new UserException("Failed to initiate registration", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public User verifyAndRegister(VerificationRequest request) {
        EmailVerificationToken token = tokenRepository.findByEmailAndCodeAndUsedFalse(request.getEmail(), request.getCode())
                .orElseThrow(() -> new UserException("Invalid verification code", HttpStatus.BAD_REQUEST));

        if (token.isExpired()) {
            throw new UserException("Verification code has expired", HttpStatus.BAD_REQUEST);
        }

        User user = token.getUser();
        user.setActive(true);
        token.setUsed(true);

        try {
            tokenRepository.save(token);
            return userRepository.save(user);
        } catch (Exception e) {
            log.error("Failed to complete registration for user {}: {}", request.getEmail(), e.getMessage());
            throw new UserException("Failed to complete registration", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 6-digit code
        return String.valueOf(code);
    }

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        return LoginResponse.builder()
                .accessToken(jwt)
                .build();
    }

    public UserDetailsResponse verifyTokenAndGetDetails(String token) {
        if (!tokenProvider.validateToken(token)) {
            throw new UserException("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        String username = tokenProvider.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));
        
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return UserDetailsResponse.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .userType(user.getRoles().iterator().next())
                .refreshToken(refreshToken.getToken())
                .build();
    }

    public String initiatePasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserException("User not found with this email", HttpStatus.NOT_FOUND));

        try {
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

            log.info("Password reset code sent for user: {}", request.getEmail());
            return "Password reset code sent successfully";
        } catch (Exception e) {
            log.error("Failed to process password reset for {}: {}", request.getEmail(), e.getMessage());
            throw new UserException("Failed to process password reset request", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public String resetPassword(PasswordResetConfirmRequest request) {
        try {
            EmailVerificationToken token = tokenRepository.findByEmailAndCodeAndUsedFalse(
                    request.getEmail(), request.getCode())
                    .orElseThrow(() -> new UserException("Invalid reset code", HttpStatus.BAD_REQUEST));

            if (token.isExpired()) {
                throw new UserException("Reset code has expired", HttpStatus.BAD_REQUEST);
            }

            User user = token.getUser();
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            token.setUsed(true);

            tokenRepository.save(token);
            userRepository.save(user);
            
            log.info("Password reset successful for user: {}", request.getEmail());
            return "Password reset successful";
        } catch (UserException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to reset password for {}: {}", request.getEmail(), e.getMessage());
            throw new UserException("Failed to reset password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
} 