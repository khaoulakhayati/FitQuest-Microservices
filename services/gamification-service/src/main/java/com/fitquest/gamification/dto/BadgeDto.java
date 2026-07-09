package com.fitquest.gamification.dto;

import java.time.Instant;

public record BadgeDto(
        Long id,
        String code,
        String name,
        String description,
        String iconUrl,
        String category,
        int xpThreshold,
        Instant createdAt,
        boolean unlocked
) {
}
