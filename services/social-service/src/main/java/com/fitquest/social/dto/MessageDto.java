package com.fitquest.social.dto;

import java.time.Instant;

public record MessageDto(
        String id,
        Long senderId,
        Long receiverId,
        String content,
        boolean read,
        Instant sentAt
) {
}
