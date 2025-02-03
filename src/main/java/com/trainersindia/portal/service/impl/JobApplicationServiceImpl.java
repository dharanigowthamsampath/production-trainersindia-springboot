package com.trainersindia.portal.service.impl;

import com.trainersindia.portal.dto.JobApplicationRequest;
import com.trainersindia.portal.dto.JobApplicationResponse;
import com.trainersindia.portal.entity.*;
import com.trainersindia.portal.exception.UserException;
import com.trainersindia.portal.repository.JobApplicationRepository;
import com.trainersindia.portal.repository.PostRepository;
import com.trainersindia.portal.repository.TrainerProfileRepository;
import com.trainersindia.portal.repository.UserRepository;
import com.trainersindia.portal.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final TrainerProfileRepository profileRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public JobApplicationResponse apply(JobApplicationRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        TrainerProfile trainer = profileRepository.findByUser(user)
                .orElseThrow(() -> new UserException("Trainer profile not found", HttpStatus.NOT_FOUND));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new UserException("Job post not found", HttpStatus.NOT_FOUND));

        if (post.getApplicationDeadline().isBefore(LocalDate.now())) {
            throw new UserException("Application deadline has passed", HttpStatus.BAD_REQUEST);
        }

        if (applicationRepository.existsByTrainerAndPost(trainer, post)) {
            throw new UserException("You have already applied for this job", HttpStatus.CONFLICT);
        }

        JobApplication application = new JobApplication();
        application.setTrainer(trainer);
        application.setPost(post);
        application.setCoverLetter(request.getCoverLetter());
        application.setStatus(ApplicationStatus.PENDING);

        return JobApplicationResponse.fromEntity(applicationRepository.save(application));
    }

    @Override
    @Transactional
    public JobApplicationResponse withdraw(Long applicationId, String username) {
        JobApplication application = getApplicationForTrainer(applicationId, username);

        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new UserException("Cannot withdraw application in current status", HttpStatus.BAD_REQUEST);
        }

        application.setStatus(ApplicationStatus.WITHDRAWN);
        return JobApplicationResponse.fromEntity(applicationRepository.save(application));
    }

    @Override
    @Transactional
    public JobApplicationResponse updateStatus(Long applicationId, ApplicationStatus status, String username) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new UserException("Application not found", HttpStatus.NOT_FOUND));

        if (!application.getPost().getPostedBy().equals(username)) {
            throw new UserException("Not authorized to update this application", HttpStatus.FORBIDDEN);
        }

        application.setStatus(status);
        return JobApplicationResponse.fromEntity(applicationRepository.save(application));
    }

    @Override
    public List<JobApplicationResponse> getTrainerApplications(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException("User not found", HttpStatus.NOT_FOUND));

        TrainerProfile trainer = profileRepository.findByUser(user)
                .orElseThrow(() -> new UserException("Trainer profile not found", HttpStatus.NOT_FOUND));

        return applicationRepository.findByTrainer(trainer).stream()
                .map(JobApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobApplicationResponse> getJobApplications(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new UserException("Job post not found", HttpStatus.NOT_FOUND));

        if (!post.getPostedBy().equals(username)) {
            throw new UserException("Not authorized to view applications for this post", HttpStatus.FORBIDDEN);
        }

        return applicationRepository.findByPost(post).stream()
                .map(JobApplicationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public JobApplicationResponse getApplication(Long applicationId, String username) {
        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new UserException("Application not found", HttpStatus.NOT_FOUND));

        if (!application.getTrainer().getUser().getUsername().equals(username) &&
            !application.getPost().getPostedBy().equals(username)) {
            throw new UserException("Not authorized to view this application", HttpStatus.FORBIDDEN);
        }

        return JobApplicationResponse.fromEntity(application);
    }

    private JobApplication getApplicationForTrainer(Long applicationId, String username) {

        JobApplication application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new UserException("Application not found", HttpStatus.NOT_FOUND));

        if (!application.getTrainer().getUser().getUsername().equals(username)) {
            throw new UserException("Not authorized to access this application", HttpStatus.FORBIDDEN);
        }

        return application;
    }
} 