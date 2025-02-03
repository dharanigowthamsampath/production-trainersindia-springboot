package com.trainersindia.portal.repository;

import com.trainersindia.portal.entity.TrainerProfile;
import com.trainersindia.portal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerProfileRepository extends JpaRepository<TrainerProfile, Long> {
    Optional<TrainerProfile> findByUser(User user);
    Optional<TrainerProfile> findByEmail(String email);
    boolean existsByUser(User user);
} 