package com.fitquest.gamification.dto;

import java.time.Instant;

public record AchievementDto(
        Long id,
        Long userId,
        BadgeDto badge,
        String unlockReason,
        Instant unlockedAt
) {
}
