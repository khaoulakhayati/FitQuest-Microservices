package com.fitquest.gamification.dto;

import java.time.Instant;

public record XpHistoryDto(
        Long id,
        int amount,
        String source,
        String referenceId,
        String description,
        Instant createdAt
) {
}
