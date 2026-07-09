package com.fitquest.challenge.dto;

import com.fitquest.challenge.entity.ChallengeStatus;
import com.fitquest.challenge.entity.ChallengeType;

import java.time.Instant;

public record ChallengeDto(
        String id,
        String title,
        String description,
        ChallengeType type,
        ChallengeStatus status,
        Instant startDate,
        Instant endDate,
        int goalPoints,
        Instant createdAt,
        Instant updatedAt
) {
}
