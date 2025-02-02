package com.trainersindia.portal.controller;

import com.trainersindia.portal.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class HelloWorldController {

    private final EmailService emailService;

    @GetMapping("/hello")
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Hello, World!");
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(
            @RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestParam("body") String body,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment
    ) {
        log.info("Received request to send email to: {}", to);
        
        try {
            if (attachment != null && !attachment.isEmpty()) {
                emailService.sendEmailWithAttachment(to, subject, body, attachment);
            } else {
                emailService.sendSimpleEmail(to, subject, body);
            }
            return ResponseEntity.ok("Email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send email", e);
            return ResponseEntity.internalServerError().body("Failed to send email: " + e.getMessage());
        }
    }
}
