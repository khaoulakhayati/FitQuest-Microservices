package com.fitquest.workout.repository;

import com.fitquest.workout.entity.Workout;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    Optional<Workout> findByName(String name);

    @EntityGraph(attributePaths = "exercises")
    List<Workout> findAllByOrderByNameAsc();

    @EntityGraph(attributePaths = "exercises")
    List<Workout> findBySharedTemplateTrueOrUserIdOrderByNameAsc(Long userId);

    @EntityGraph(attributePaths = "exercises")
    @Query("SELECT w FROM Workout w WHERE w.id = :id")
    Optional<Workout> findByIdWithExercises(@Param("id") Long id);

    @EntityGraph(attributePaths = "exercises")
    @Query("SELECT w FROM Workout w WHERE w.id = :id AND w.userId = :userId")
    Optional<Workout> findByIdAndUserIdWithExercises(@Param("id") Long id, @Param("userId") Long userId);
}
