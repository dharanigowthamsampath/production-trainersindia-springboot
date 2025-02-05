package com.trainersindia.portal.service.impl;

import com.trainersindia.portal.dto.TokenResponse;
import com.trainersindia.portal.entity.RefreshToken;
import com.trainersindia.portal.entity.User;
import com.trainersindia.portal.exception.UserException;
import com.trainersindia.portal.repository.RefreshTokenRepository;
import com.trainersindia.portal.security.JwtTokenProvider;
import com.trainersindia.portal.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider tokenProvider;

    @Value("${app.jwt.refresh-token.expiration}")
    private long refreshTokenExpiration;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(User user) {
        // Revoke any existing refresh tokens for the user
        refreshTokenRepository.revokeAllUserTokens(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenExpiration));

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshToken verifyRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UserException("Invalid refresh token", HttpStatus.UNAUTHORIZED));

        if (refreshToken.isRevoked()) {
            throw new UserException("Refresh token has been revoked", HttpStatus.UNAUTHORIZED);
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new UserException("Refresh token has expired", HttpStatus.UNAUTHORIZED);
        }

        return refreshToken;
    }

    @Override
    @Transactional
    public TokenResponse refreshAccessToken(String refreshToken) {
        RefreshToken verifiedToken = verifyRefreshToken(refreshToken);
        User user = verifiedToken.getUser();

        // Generate new access token
        String accessToken = tokenProvider.generateToken(user);
        long expiresIn = tokenProvider.getExpirationFromToken(accessToken).getTime() - System.currentTimeMillis();

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn / 1000) // Convert to seconds
                .build();
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new UserException("Invalid refresh token", HttpStatus.NOT_FOUND));
        
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllUserTokens(user);
    }

    @Override
    @Transactional
    @Scheduled(cron = "0 0 0 * * ?") // Run daily at midnight
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteAllExpiredTokens(Instant.now());
    }
} 