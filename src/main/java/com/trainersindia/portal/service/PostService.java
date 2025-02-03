package com.trainersindia.portal.service;

import com.trainersindia.portal.dto.PostRequest;
import com.trainersindia.portal.entity.Post;

import java.util.List;

public interface PostService {
    Post createPost(PostRequest postRequest, String postedBy);
    Post updatePost(Long id, PostRequest postRequest, String postedBy);
    void deletePost(Long id, String postedBy);
    Post getPost(Long id);
    List<Post> getAllPosts();
    List<Post> getPostsByCompany(String postedBy);
    
    // New methods for trainers
    List<Post> searchJobs(String location, String jobType, String experienceLevel, List<String> skills);
    List<Post> getRecentJobs();
    List<Post> searchJobsByKeyword(String keyword);
} 