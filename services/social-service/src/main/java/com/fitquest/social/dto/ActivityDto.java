package com.fitquest.social.dto;

import com.fitquest.social.entity.ActivityType;

import java.time.Instant;
import java.util.Map;

public record ActivityDto(
        String id,
        Long userId,
        ActivityType activityType,
        String summary,
        Map<String, Object> metadata,
        Instant createdAt
) {
}
