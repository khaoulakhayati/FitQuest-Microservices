package com.fitquest.workout.service;

import com.fitquest.workout.dto.*;
import com.fitquest.workout.entity.Exercise;
import com.fitquest.workout.entity.Workout;
import com.fitquest.workout.entity.WorkoutSession;
import com.fitquest.workout.exception.BadRequestException;
import com.fitquest.workout.exception.NotFoundException;
import com.fitquest.workout.feign.NutritionClient;
import com.fitquest.workout.mapper.WorkoutMapper;
import com.fitquest.workout.mapper.WorkoutSessionMapper;
import com.fitquest.workout.messaging.WorkoutEventPublisher;
import com.fitquest.workout.repository.ExerciseRepository;
import com.fitquest.workout.repository.WorkoutRepository;
import com.fitquest.workout.repository.WorkoutSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WorkoutService {

    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutSessionRepository sessionRepository;
    private final WorkoutMapper workoutMapper;
    private final WorkoutSessionMapper sessionMapper;
    private final WorkoutEventPublisher eventPublisher;
    private final NutritionClient nutritionClient;

    @Transactional(readOnly = true)
    public List<WorkoutDto> getAllWorkouts(Long userId) {
        if (userId == null) {
            return workoutMapper.toDtoList(workoutRepository.findAllByOrderByNameAsc());
        }
        return workoutMapper.toDtoList(workoutRepository.findBySharedTemplateTrueOrUserIdOrderByNameAsc(userId));
    }

    @Transactional
    public WorkoutDto createWorkout(Long userId, CreateWorkoutRequest request) {
        Set<Exercise> exercises = resolveExercises(request);
        Workout workout = Workout.builder()
                .userId(userId)
                .name(request.getName())
                .description(request.getDescription())
                .difficulty(request.getDifficulty())
                .estimatedDurationMinutes(request.getEstimatedDurationMinutes())
                .exercises(exercises)
                .build();
        return workoutMapper.toDto(workoutRepository.save(workout));
    }

    @Transactional
    public WorkoutDto updateWorkout(Long userId, Long id, CreateWorkoutRequest request) {
        Workout workout = workoutRepository.findByIdAndUserIdWithExercises(id, userId)
                .orElseThrow(() -> new NotFoundException("Workout not found: " + id));
        workout.setName(request.getName());
        workout.setDescription(request.getDescription());
        workout.setDifficulty(request.getDifficulty());
        workout.setEstimatedDurationMinutes(request.getEstimatedDurationMinutes());
        workout.setExercises(resolveExercises(request));
        return workoutMapper.toDto(workoutRepository.save(workout));
    }

    @Transactional
    public void deleteWorkout(Long userId, Long id) {
        Workout workout = workoutRepository.findByIdAndUserIdWithExercises(id, userId)
                .orElseThrow(() -> new NotFoundException("Workout not found: " + id));
        sessionRepository.deleteByWorkoutId(id);
        workoutRepository.delete(workout);
    }

    @Transactional
    public WorkoutSessionDto logWorkout(Long userId, LogWorkoutRequest request) {
        Workout workout = workoutRepository.findByIdWithExercises(request.getWorkoutId())
                .orElseThrow(() -> new NotFoundException("Workout not found: " + request.getWorkoutId()));

        int durationMinutes = request.getDurationMinutes() != null
                ? request.getDurationMinutes()
                : workout.getEstimatedDurationMinutes();
        double calories = request.getCaloriesBurned() != null
                ? request.getCaloriesBurned()
                : estimateCalories(workout, durationMinutes);

        WorkoutSession session = WorkoutSession.builder()
                .userId(userId)
                .workout(workout)
                .durationMinutes(durationMinutes)
                .caloriesBurned(calories)
                .sets(request.getSets())
                .reps(request.getReps())
                .weightKg(request.getWeightKg())
                .notes(request.getNotes())
                .build();
        session = sessionRepository.save(session);

        eventPublisher.publishWorkoutLogged(userId, workout.getId(), calories);
        return sessionMapper.toDto(session);
    }

    @Transactional(readOnly = true)
    public List<WorkoutSessionDto> getHistory(Long userId) {
        return sessionMapper.toDtoList(sessionRepository.findByUserIdOrderByLoggedAtDesc(userId));
    }

    public NutritionClient.DailyNutritionResponse getDailyNutrition(Long userId, LocalDate date) {
        return nutritionClient.getDailyNutrition(userId, date);
    }

    private Set<Exercise> resolveExercises(CreateWorkoutRequest request) {
        List<Long> exerciseIds = new ArrayList<>();
        if (request.getExerciseIds() != null) {
            exerciseIds.addAll(request.getExerciseIds());
        }
        if (request.getExerciseId() != null) {
            exerciseIds.add(request.getExerciseId());
        }
        Set<Exercise> exercises = new HashSet<>();
        for (Long id : exerciseIds) {
            Exercise exercise = exerciseRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Exercise not found: " + id));
            exercises.add(exercise);
        }
        if (exercises.isEmpty()) {
            throw new BadRequestException("At least one exercise is required");
        }
        return exercises;
    }

    private double estimateCalories(Workout workout, int durationMinutes) {
        if (workout.getExercises().isEmpty()) {
            return durationMinutes * 5.0;
        }
        double avgCaloriesPerMinute = workout.getExercises().stream()
                .mapToDouble(Exercise::getCaloriesPerMinute)
                .average()
                .orElse(5.0);
        return Math.round(avgCaloriesPerMinute * durationMinutes * 10.0) / 10.0;
    }
}
