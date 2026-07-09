package com.fitquest.workout.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class WorkoutDto {
    Long id;
    String name;
    String description;
    String difficulty;
    Integer estimatedDurationMinutes;
    Long userId;
    boolean sharedTemplate;
    List<ExerciseDto> exercises;
}
