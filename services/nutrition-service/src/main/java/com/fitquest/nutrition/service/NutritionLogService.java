package com.fitquest.nutrition.service;

import com.fitquest.nutrition.entity.Meal;
import com.fitquest.nutrition.entity.NutritionLog;
import com.fitquest.nutrition.repository.MealRepository;
import com.fitquest.nutrition.repository.NutritionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class NutritionLogService {

    private final NutritionLogRepository nutritionLogRepository;
    private final MealRepository mealRepository;

    @Transactional
    public void refreshDailyLog(Long userId, LocalDate date) {
        Instant start = date.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant end = date.plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);

        var meals = mealRepository.findByUserIdAndConsumedAtBetweenOrderByConsumedAtAsc(userId, start, end);

        int calories = 0;
        double protein = 0;
        double carbs = 0;
        double fat = 0;

        for (Meal meal : meals) {
            calories += meal.getTotalCalories();
            protein += meal.getTotalProteinG();
            carbs += meal.getTotalCarbsG();
            fat += meal.getTotalFatG();
        }

        NutritionLog log = nutritionLogRepository.findByUserIdAndLogDate(userId, date)
                .orElse(NutritionLog.builder().userId(userId).logDate(date).build());

        log.setTotalCalories(calories);
        log.setTotalProteinG(Math.round(protein * 10.0) / 10.0);
        log.setTotalCarbsG(Math.round(carbs * 10.0) / 10.0);
        log.setTotalFatG(Math.round(fat * 10.0) / 10.0);
        log.setMealCount(meals.size());

        nutritionLogRepository.save(log);
    }

    public LocalDate toLogDate(Instant consumedAt) {
        return consumedAt.atZone(ZoneOffset.UTC).toLocalDate();
    }
}
