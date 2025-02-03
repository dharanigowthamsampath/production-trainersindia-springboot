package com.trainersindia.portal.controller.trainer;

import com.trainersindia.portal.entity.Post;
import com.trainersindia.portal.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trainer/jobs")
@PreAuthorize("hasRole('TRAINER')")
@RequiredArgsConstructor
public class TrainerJobController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<Post>> getAllJobs(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) List<String> skills
    ) {
        List<Post> posts = postService.searchJobs(location, jobType, experienceLevel, skills);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getJobDetails(@PathVariable Long id) {
        Post post = postService.getPost(id);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<Post>> getRecentJobs() {
        List<Post> recentPosts = postService.getRecentJobs();
        return ResponseEntity.ok(recentPosts);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Post>> searchJobs(@RequestParam String keyword) {
        List<Post> searchResults = postService.searchJobsByKeyword(keyword);
        return ResponseEntity.ok(searchResults);
    }
} 