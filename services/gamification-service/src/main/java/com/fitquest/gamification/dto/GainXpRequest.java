package com.fitquest.gamification.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GainXpRequest(
        @NotNull @Min(1) @Max(10000) Integer amount,
        @NotBlank String reason,
        String referenceId
) {
}
