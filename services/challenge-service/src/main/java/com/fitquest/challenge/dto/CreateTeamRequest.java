package com.fitquest.challenge.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTeamRequest(
        @NotBlank String challengeId,
        @NotBlank String name,
        String motto
) {
}
