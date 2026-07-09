package com.fitquest.nutrition.repository;

import com.fitquest.nutrition.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MealRepository extends JpaRepository<Meal, Long> {

    List<Meal> findByUserIdOrderByConsumedAtDesc(Long userId);

    Optional<Meal> findByIdAndUserId(Long id, Long userId);

    List<Meal> findByUserIdAndConsumedAtBetweenOrderByConsumedAtAsc(
            Long userId, Instant start, Instant end);
}
