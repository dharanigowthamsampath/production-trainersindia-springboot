package com.trainersindia.portal.service;

import com.trainersindia.portal.dto.TokenResponse;
import com.trainersindia.portal.entity.RefreshToken;
import com.trainersindia.portal.entity.User;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyRefreshToken(String token);
    TokenResponse refreshAccessToken(String refreshToken);
    void revokeRefreshToken(String token);
    void revokeAllUserTokens(User user);
    void deleteExpiredTokens();
} 