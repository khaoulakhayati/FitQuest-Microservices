package com.fitquest.social.controller;

import com.fitquest.social.dto.CreatePostRequest;
import com.fitquest.social.dto.SocialPostDto;
import com.fitquest.social.entity.VoteType;
import com.fitquest.social.service.SocialPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/social/posts")
@RequiredArgsConstructor
public class SocialPostController {

    private final SocialPostService postService;

    @GetMapping("/feed")
    public List<SocialPostDto> feed(@RequestHeader("X-User-Id") Long userId) {
        return postService.feed(userId);
    }

    @PostMapping("/groups/{groupId}")
    @ResponseStatus(HttpStatus.CREATED)
    public SocialPostDto create(@RequestHeader("X-User-Id") Long userId,
                                @RequestHeader(value = "X-User-Email", required = false) String email,
                                @PathVariable String groupId,
                                @Valid @RequestBody CreatePostRequest request) {
        String username = email != null && email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        return postService.create(userId, username, groupId, request);
    }

    @PostMapping("/{postId}/upvote")
    public SocialPostDto upvote(@RequestHeader("X-User-Id") Long userId, @PathVariable String postId) {
        return postService.react(userId, postId, VoteType.UP);
    }

    @PostMapping("/{postId}/downvote")
    public SocialPostDto downvote(@RequestHeader("X-User-Id") Long userId, @PathVariable String postId) {
        return postService.react(userId, postId, VoteType.DOWN);
    }
}
