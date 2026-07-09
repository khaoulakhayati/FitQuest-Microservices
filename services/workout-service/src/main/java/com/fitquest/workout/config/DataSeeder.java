package com.fitquest.workout.config;

import com.fitquest.workout.entity.Exercise;
import com.fitquest.workout.entity.Workout;
import com.fitquest.workout.repository.ExerciseRepository;
import com.fitquest.workout.repository.WorkoutRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashSet;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder {

    private final ExerciseRepository exerciseRepository;
    private final WorkoutRepository workoutRepository;

    @Bean
    CommandLineRunner seedWorkoutData() {
        return args -> {
            Exercise pushUps = exercise("Push-ups", "Classic bodyweight chest and triceps exercise", "CHEST", "BEGINNER", 8.0);
            Exercise benchPress = exercise("Bench Press", "Barbell press for chest strength", "CHEST", "INTERMEDIATE", 8.5);
            Exercise deadlift = exercise("Deadlift", "Posterior chain strength lift", "BACK", "ADVANCED", 9.0);
            Exercise latPulldown = exercise("Lat Pulldown", "Cable pull for lats and upper back", "BACK", "BEGINNER", 6.5);
            Exercise shoulderPress = exercise("Shoulder Press", "Vertical press for shoulders", "SHOULDERS", "INTERMEDIATE", 7.0);
            Exercise lateralRaise = exercise("Lateral Raise", "Isolation movement for side delts", "SHOULDERS", "BEGINNER", 4.5);
            Exercise bicepsCurl = exercise("Biceps Curl", "Dumbbell curl for biceps", "BICEPS", "BEGINNER", 4.5);
            Exercise hammerCurl = exercise("Hammer Curl", "Neutral-grip curl for arms", "BICEPS", "BEGINNER", 4.8);
            Exercise tricepsPushdown = exercise("Triceps Pushdown", "Cable isolation for triceps", "TRICEPS", "BEGINNER", 4.8);
            Exercise squats = exercise("Squats", "Lower body compound movement", "LEGS", "BEGINNER", 7.5);
            Exercise lunges = exercise("Lunges", "Single-leg strength movement", "LEGS", "BEGINNER", 6.5);
            Exercise hipThrust = exercise("Hip Thrust", "Glute-focused hip extension", "GLUTES", "INTERMEDIATE", 6.0);
            Exercise plank = exercise("Plank", "Core stability hold", "ABS", "BEGINNER", 4.0);
            Exercise crunches = exercise("Crunches", "Abdominal flexion exercise", "ABS", "BEGINNER", 4.0);
            Exercise running = exercise("Running", "Steady-state cardio", "CARDIO", "INTERMEDIATE", 11.0);
            Exercise burpees = exercise("Burpees", "Full-body high-intensity exercise", "FULL_BODY", "INTERMEDIATE", 12.0);

            workout("HIIT Blast", "High-intensity interval circuit for fat burn", "INTERMEDIATE", 25,
                    burpees, squats, pushUps);
            workout("Core Crusher", "Focused core and upper-body stability session", "BEGINNER", 20,
                    plank, crunches, pushUps);
            workout("Cardio Endurance", "Steady cardio for endurance building", "INTERMEDIATE", 40,
                    running);
            workout("Strength Foundation", "Compound lifts for strength and muscle", "ADVANCED", 45,
                    deadlift, squats, benchPress);
            workout("Back Builder", "Back-focused pull session", "INTERMEDIATE", 35,
                    deadlift, latPulldown);
            workout("Arm Day", "Biceps and triceps accessory work", "BEGINNER", 30,
                    bicepsCurl, hammerCurl, tricepsPushdown);
            workout("Shoulder Stability", "Shoulder strength and control", "BEGINNER", 30,
                    shoulderPress, lateralRaise);
            workout("Lower Body", "Leg and glute strength session", "INTERMEDIATE", 40,
                    squats, lunges, hipThrust);

            log.info("Workout service seed data loaded ({} exercises, {} workouts)",
                    exerciseRepository.count(), workoutRepository.count());
        };
    }

    private Exercise exercise(String name, String description, String muscleGroup, String difficulty, double caloriesPerMinute) {
        return exerciseRepository.findByName(name)
                .orElseGet(() -> exerciseRepository.save(Exercise.builder()
                        .name(name)
                        .description(description)
                        .muscleGroup(muscleGroup)
                        .difficulty(difficulty)
                        .caloriesPerMinute(caloriesPerMinute)
                        .build()));
    }

    private void workout(String name, String description, String difficulty, int durationMinutes, Exercise... exercises) {
        Workout workout = workoutRepository.findByName(name).orElseGet(() -> Workout.builder()
                .name(name)
                .description(description)
                .difficulty(difficulty)
                .estimatedDurationMinutes(durationMinutes)
                .exercises(new LinkedHashSet<>(java.util.List.of(exercises)))
                .build());
        workout.setSharedTemplate(true);
        workoutRepository.save(workout);
    }
}
