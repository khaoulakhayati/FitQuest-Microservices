package com.fitquest.social.dto;

import com.fitquest.social.entity.NotificationType;

import java.time.Instant;
import java.util.Map;

public record NotificationDto(
        String id,
        Long userId,
        NotificationType type,
        String title,
        String message,
        Map<String, Object> payload,
        boolean read,
        Instant createdAt
) {
}
