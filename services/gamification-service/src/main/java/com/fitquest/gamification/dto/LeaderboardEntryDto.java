package com.fitquest.gamification.dto;

public record LeaderboardEntryDto(
        int rank,
        Long userId,
        long totalXp,
        int level,
        long achievementCount
) {
}
