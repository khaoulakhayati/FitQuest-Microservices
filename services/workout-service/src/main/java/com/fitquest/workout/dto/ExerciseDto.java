package com.fitquest.workout.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ExerciseDto {
    Long id;
    String name;
    String description;
    String muscleGroup;
    String difficulty;
    Double caloriesPerMinute;
}
