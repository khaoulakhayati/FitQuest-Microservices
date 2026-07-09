package com.fitquest.workout.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class WorkoutSessionDto {
    Long id;
    Long userId;
    Long workoutId;
    String workoutName;
    Integer durationMinutes;
    Double caloriesBurned;
    Integer sets;
    Integer reps;
    Double weightKg;
    String notes;
    Instant loggedAt;
}
