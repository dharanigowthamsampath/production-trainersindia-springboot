package com.trainersindia.portal.repository;

import com.trainersindia.portal.entity.JobApplication;
import com.trainersindia.portal.entity.Post;
import com.trainersindia.portal.entity.TrainerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByTrainer(TrainerProfile trainer);
    List<JobApplication> findByPost(Post post);
    Optional<JobApplication> findByTrainerAndPost(TrainerProfile trainer, Post post);
    boolean existsByTrainerAndPost(TrainerProfile trainer, Post post);
} 