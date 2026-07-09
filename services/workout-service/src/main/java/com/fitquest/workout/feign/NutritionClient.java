package com.fitquest.workout.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@FeignClient(name = "nutrition-service")
public interface NutritionClient {

    @GetMapping("/nutrition/daily")
    DailyNutritionResponse getDailyNutrition(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date);

    record DailyNutritionResponse(
            LocalDate date,
            Integer totalCalories,
            Double totalProteinG,
            Double totalCarbsG,
            Double totalFatG,
            Integer mealCount,
            List<MealResponse> meals) {
    }

    record MealResponse(
            Long id,
            String name,
            String type,
            LocalDate date,
            Integer totalCalories,
            Double totalProteinG,
            Double totalCarbsG,
            Double totalFatG) {
    }
}
