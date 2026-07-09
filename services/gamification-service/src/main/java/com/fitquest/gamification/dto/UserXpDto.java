package com.fitquest.gamification.dto;

import java.util.List;

public record UserXpDto(
        Long userId,
        long totalXp,
        int level,
        int xpToNextLevel,
        List<XpHistoryDto> recentHistory
) {
}
