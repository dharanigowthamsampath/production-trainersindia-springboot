package com.trainersindia.portal.repository;

import com.trainersindia.portal.entity.EmailVerificationToken;
import com.trainersindia.portal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByEmailAndCodeAndUsedFalse(String email, String code);
    Optional<EmailVerificationToken> findByUser(User user);
} 