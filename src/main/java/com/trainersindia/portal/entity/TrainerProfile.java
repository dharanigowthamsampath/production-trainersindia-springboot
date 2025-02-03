package com.trainersindia.portal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "trainer_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(nullable = false)
    private String location;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String experience;

    @ElementCollection
    @CollectionTable(name = "trainer_skills", joinColumns = @JoinColumn(name = "trainer_id"))
    @Column(name = "skill")
    private List<String> skills;

    private String education;

    @ElementCollection
    @CollectionTable(name = "trainer_certifications", joinColumns = @JoinColumn(name = "trainer_id"))
    @Column(name = "certification")
    private List<String> certifications;

    @Column(name = "previous_trainings", columnDefinition = "TEXT")
    private String previousTrainings;

    private String availability;

    @Column(name = "expected_salary")
    private String expectedSalary;

    @Column(name = "resume_url")
    private String resumeUrl;

    @Column(name = "linkedin_url")
    private String linkedinUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 