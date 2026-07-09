package com.fitquest.workout.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

@Data
public class CreateWorkoutRequest {

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String difficulty;

    @NotNull
    @Positive
    private Integer estimatedDurationMinutes;

    private List<Long> exerciseIds;

    private Long exerciseId;
}
