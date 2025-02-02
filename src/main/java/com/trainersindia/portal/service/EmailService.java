package com.trainersindia.portal.service;

import org.springframework.web.multipart.MultipartFile;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String code);
    void sendSimpleEmail(String to, String subject, String body);
    void sendEmailWithAttachment(String to, String subject, String body, MultipartFile attachment);
    void sendPasswordResetEmail(String toEmail, String code);
} 