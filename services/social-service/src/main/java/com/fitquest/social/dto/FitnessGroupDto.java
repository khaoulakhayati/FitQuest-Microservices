package com.fitquest.social.dto;

import java.time.Instant;

public record FitnessGroupDto(
        String id,
        String name,
        String description,
        Long coachId,
        String weeklyWorkoutPlan,
        int memberCount,
        Instant createdAt
) {
}
