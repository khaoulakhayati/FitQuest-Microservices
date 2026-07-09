package com.fitquest.challenge.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddPointsRequest(
        @NotNull Long userId,
        @Min(1) int points
) {
}
