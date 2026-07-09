package com.fitquest.challenge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JoinChallengeRequest(
        @NotNull Long userId,
        @NotBlank String displayName,
        String teamId
) {
}
