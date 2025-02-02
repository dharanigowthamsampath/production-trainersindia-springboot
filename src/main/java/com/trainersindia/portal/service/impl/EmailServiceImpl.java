package com.trainersindia.portal.service.impl;

import com.trainersindia.portal.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.UnsupportedEncodingException;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendVerificationEmail(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("noreply@trainersindia.com", "Trainers India");
            helper.setTo(toEmail);
            helper.setSubject("Verify Your Email - Trainers India");
            
            String htmlContent = String.format("""
                <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background-color: #f8f9fa; padding: 20px; text-align: center; border-radius: 5px;">
                        <h1 style="color: #0d6efd;">Trainers India</h1>
                        <h3 style="color: #333;">Email Verification</h2>
                    </div>
                    <div style="padding: 20px; background-color: white; border-radius: 5px; margin-top: 20px;">
                        <p style="font-size: 16px; color: #333;">Hello,</p>
                        <p style="font-size: 16px; color: #333;">Thank you for registering with Trainers India. To complete your registration, please use the verification code below:</p>
                        <div style="background-color: #e9ecef; padding: 15px; text-align: center; border-radius: 5px; margin: 20px 0;">
                            <h2 style="color: #0d6efd; letter-spacing: 5px; margin: 0;">%s</h2>
                        </div>
                        <p style="font-size: 14px; color: #666;">This code will expire in 15 minutes.</p>
                        <p style="font-size: 14px; color: #666;">If you didn't request this verification, please ignore this email.</p>
                    </div>
                    <div style="text-align: center; margin-top: 20px; color: #666; font-size: 12px;">
                        <p>© 2024 Trainers India. All rights reserved.</p>
                    </div>
                </div>
                """, code);
            
            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Verification email sent successfully to: {}", toEmail);
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send verification email to: {}", toEmail, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    @Override
    public void sendSimpleEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String body, MultipartFile attachment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            if (attachment != null && !attachment.isEmpty()) {
                helper.addAttachment(attachment.getOriginalFilename(), attachment);
            }

            mailSender.send(message);
            log.info("Email with attachment sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email with attachment to: {}", to, e);
            throw new RuntimeException("Failed to send email with attachment", e);
        }
    }
} 