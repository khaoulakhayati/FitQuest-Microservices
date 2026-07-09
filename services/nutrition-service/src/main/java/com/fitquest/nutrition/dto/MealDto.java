package com.fitquest.nutrition.dto;

import com.fitquest.nutrition.entity.MealType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class MealDto {

    private Long id;
    private String name;
    private MealType mealType;
    private Instant consumedAt;
    private List<MealItemDto> items;
    private Integer totalCalories;
    private Double totalProteinG;
    private Double totalCarbsG;
    private Double totalFatG;
    private Instant createdAt;
}
