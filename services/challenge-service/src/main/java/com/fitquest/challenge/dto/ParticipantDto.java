package com.fitquest.challenge.dto;

import java.time.Instant;

public record ParticipantDto(
        String id,
        String challengeId,
        Long userId,
        String teamId,
        String displayName,
        int points,
        Instant joinedAt,
        Instant updatedAt
) {
}
