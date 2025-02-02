package com.trainersindia.portal.repository;

import com.trainersindia.portal.entity.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByEmailAndCodeAndUsedFalse(String email, String code);
} 