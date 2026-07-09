package com.fitquest.social.dto;

import java.time.Instant;

public record SocialPostDto(
        String id,
        String groupId,
        Long authorId,
        String authorUsername,
        String content,
        int upvotes,
        int downvotes,
        String myVote,
        Instant createdAt
) {
}
