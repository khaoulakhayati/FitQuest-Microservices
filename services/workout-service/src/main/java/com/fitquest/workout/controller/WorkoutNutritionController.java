package com.fitquest.workout.controller;

import com.fitquest.workout.feign.NutritionClient;
import com.fitquest.workout.service.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/workouts/nutrition")
@RequiredArgsConstructor
@Tag(name = "Workout Nutrition")
public class WorkoutNutritionController {

    private final WorkoutService workoutService;

    @GetMapping("/daily")
    @Operation(summary = "Fetch the current user's daily nutrition through OpenFeign")
    public NutritionClient.DailyNutritionResponse getDailyNutrition(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return workoutService.getDailyNutrition(userId, date);
    }
}
