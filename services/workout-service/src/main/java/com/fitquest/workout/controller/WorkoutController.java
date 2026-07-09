package com.fitquest.workout.controller;

import com.fitquest.workout.dto.*;
import com.fitquest.workout.service.WorkoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/workouts")
@RequiredArgsConstructor
@Tag(name = "Workouts")
public class WorkoutController {

    private final WorkoutService workoutService;

    @GetMapping
    @Operation(summary = "List all workout templates")
    public List<WorkoutDto> getWorkouts(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return workoutService.getAllWorkouts(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a workout template")
    public WorkoutDto createWorkout(@RequestHeader("X-User-Id") Long userId,
                                    @Valid @RequestBody CreateWorkoutRequest request) {
        return workoutService.createWorkout(userId, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a workout template")
    public WorkoutDto updateWorkout(@RequestHeader("X-User-Id") Long userId,
                                    @PathVariable Long id,
                                    @Valid @RequestBody CreateWorkoutRequest request) {
        return workoutService.updateWorkout(userId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a workout template")
    public void deleteWorkout(@RequestHeader("X-User-Id") Long userId,
                              @PathVariable Long id) {
        workoutService.deleteWorkout(userId, id);
    }

    @PostMapping("/log")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Log a completed workout session")
    public WorkoutSessionDto logWorkout(@RequestHeader("X-User-Id") Long userId,
                                        @Valid @RequestBody LogWorkoutRequest request) {
        return workoutService.logWorkout(userId, request);
    }

    @GetMapping("/history")
    @Operation(summary = "Get workout history for the current user")
    public List<WorkoutSessionDto> getHistory(@RequestHeader("X-User-Id") Long userId) {
        return workoutService.getHistory(userId);
    }
}
