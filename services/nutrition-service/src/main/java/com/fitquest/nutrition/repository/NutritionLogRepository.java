package com.fitquest.nutrition.repository;

import com.fitquest.nutrition.entity.NutritionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NutritionLogRepository extends JpaRepository<NutritionLog, Long> {

    Optional<NutritionLog> findByUserIdAndLogDate(Long userId, LocalDate logDate);

    List<NutritionLog> findByUserIdAndLogDateBetweenOrderByLogDateAsc(
            Long userId, LocalDate start, LocalDate end);
}
