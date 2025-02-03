package com.trainersindia.portal.service.impl;

import com.trainersindia.portal.dto.PostRequest;
import com.trainersindia.portal.entity.Post;
import com.trainersindia.portal.entity.User;
import com.trainersindia.portal.exception.UserException;
import com.trainersindia.portal.repository.PostRepository;
import com.trainersindia.portal.repository.UserRepository;
import com.trainersindia.portal.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Post createPost(PostRequest postRequest, String postedBy) {
        User company = userRepository.findByUsername(postedBy)
                .orElseThrow(() -> new UserException("Company not found", HttpStatus.NOT_FOUND));

        Post post = new Post();
        mapPostRequestToPost(postRequest, post);
        post.setPostedBy(postedBy);
        post.setCompanyName(company.getFullName());
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public Post updatePost(Long id, PostRequest postRequest, String postedBy) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new UserException("Post not found", HttpStatus.NOT_FOUND));

        if (!post.getPostedBy().equals(postedBy)) {
            throw new UserException("You are not authorized to update this post", HttpStatus.FORBIDDEN);
        }

        mapPostRequestToPost(postRequest, post);
        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(Long id, String postedBy) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new UserException("Post not found", HttpStatus.NOT_FOUND));

        if (!post.getPostedBy().equals(postedBy)) {
            throw new UserException("You are not authorized to delete this post", HttpStatus.FORBIDDEN);
        }

        postRepository.delete(post);
    }

    @Override
    public Post getPost(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new UserException("Post not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> getPostsByCompany(String postedBy) {
        return postRepository.findByPostedBy(postedBy);
    }

    @Override
    public List<Post> searchJobs(String location, String jobType, String experienceLevel, List<String> skills) {
        List<Post> posts = postRepository.findAll();
        
        return posts.stream()
            .filter(post -> location == null || post.getLocation().equalsIgnoreCase(location))
            .filter(post -> jobType == null || post.getJobType().equalsIgnoreCase(jobType))
            .filter(post -> experienceLevel == null || post.getExperienceLevel().equalsIgnoreCase(experienceLevel))
            .filter(post -> skills == null || skills.isEmpty() || post.getSkillsRequired().containsAll(skills))
            .collect(Collectors.toList());
    }

    @Override
    public List<Post> getRecentJobs() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return postRepository.findAll().stream()
            .filter(post -> post.getCreatedAt().isAfter(thirtyDaysAgo))
            .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Post> searchJobsByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPosts();
        }

        String lowercaseKeyword = keyword.toLowerCase();
        return postRepository.findAll().stream()
            .filter(post -> 
                post.getTitle().toLowerCase().contains(lowercaseKeyword) ||
                post.getDescription().toLowerCase().contains(lowercaseKeyword) ||
                post.getCompanyName().toLowerCase().contains(lowercaseKeyword) ||
                post.getSkillsRequired().stream()
                    .anyMatch(skill -> skill.toLowerCase().contains(lowercaseKeyword))
            )
            .collect(Collectors.toList());
    }

    private void mapPostRequestToPost(PostRequest postRequest, Post post) {
        post.setTitle(postRequest.getTitle());
        post.setLocation(postRequest.getLocation());
        post.setJobType(postRequest.getJobType());
        post.setExperienceLevel(postRequest.getExperienceLevel());
        post.setSalaryRange(postRequest.getSalaryRange());
        post.setDescription(postRequest.getDescription());
        post.setSkillsRequired(postRequest.getSkillsRequired());
        post.setQualifications(postRequest.getQualifications());
        post.setApplicationDeadline(postRequest.getApplicationDeadline());
        post.setContactEmail(postRequest.getContactEmail());
    }
} 