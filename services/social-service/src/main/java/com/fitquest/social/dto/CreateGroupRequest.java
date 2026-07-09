package com.fitquest.social.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateGroupRequest(
        @NotBlank String name,
        String description,
        String weeklyWorkoutPlan
) {
}
