package com.fitquest.workout.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class LogWorkoutRequest {

    @NotNull
    private Long workoutId;

    @Positive
    private Integer durationMinutes;

    @PositiveOrZero
    private Double caloriesBurned;

    @Positive
    private Integer sets;

    @Positive
    private Integer reps;

    @PositiveOrZero
    private Double weightKg;

    private String notes;
}
