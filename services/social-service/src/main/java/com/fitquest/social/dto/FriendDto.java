package com.fitquest.social.dto;

import com.fitquest.social.entity.FriendStatus;

import java.time.Instant;

public record FriendDto(
        String id,
        Long userId,
        Long friendUserId,
        String friendUsername,
        FriendStatus status,
        Instant createdAt
) {
}
