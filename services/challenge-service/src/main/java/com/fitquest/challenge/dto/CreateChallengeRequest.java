package com.fitquest.challenge.dto;

import com.fitquest.challenge.entity.ChallengeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record CreateChallengeRequest(
        @NotBlank String title,
        String description,
        @NotNull ChallengeType type,
        Instant startDate,
        Instant endDate,
        @Min(1) int goalPoints
) {
}
