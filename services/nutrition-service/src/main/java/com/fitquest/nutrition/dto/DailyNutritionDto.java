package com.fitquest.nutrition.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class DailyNutritionDto {

    private LocalDate date;
    private Integer totalCalories;
    private Double totalProteinG;
    private Double totalCarbsG;
    private Double totalFatG;
    private Integer mealCount;
    private List<MealDto> meals;
}
