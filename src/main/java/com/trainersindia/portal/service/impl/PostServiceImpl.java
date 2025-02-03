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

import java.util.List;

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