package com.fitquest.workout.repository;

import com.fitquest.workout.entity.WorkoutSession;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {

    @EntityGraph(attributePaths = "workout")
    List<WorkoutSession> findByUserIdOrderByLoggedAtDesc(Long userId);

    void deleteByWorkoutId(Long workoutId);
}
