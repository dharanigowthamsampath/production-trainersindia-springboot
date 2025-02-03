package com.trainersindia.portal.controller.company;

import com.trainersindia.portal.dto.PostRequest;
import com.trainersindia.portal.entity.Post;
import com.trainersindia.portal.security.UserPrincipal;
import com.trainersindia.portal.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/company/posts")
@PreAuthorize("hasRole('COMPANY')")
@RequiredArgsConstructor
public class CompanyPostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<Post> createPost(
            @Valid @RequestBody PostRequest postRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Post post = postService.createPost(postRequest, userPrincipal.getUsername());
        return new ResponseEntity<>(post, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody PostRequest postRequest,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        Post post = postService.updatePost(id, postRequest, userPrincipal.getUsername());
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        postService.deletePost(id, userPrincipal.getUsername());
        Map<String, String> response = new HashMap<>();
        response.put("message", "Post deleted successfully");
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Post post = postService.getPost(id);
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getCompanyPosts(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<Post> posts = postService.getPostsByCompany(userPrincipal.getUsername());
        return ResponseEntity.ok(posts);
    }
} 