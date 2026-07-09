package com.fitquest.challenge.dto;

import java.time.Instant;
import java.util.List;

public record TeamDto(
        String id,
        String challengeId,
        String name,
        String motto,
        int totalPoints,
        List<String> memberIds,
        Instant createdAt,
        Instant updatedAt
) {
}
