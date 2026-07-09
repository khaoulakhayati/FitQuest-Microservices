package com.fitquest.nutrition.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MealItemDto {

    private Long foodItemId;
    private String foodName;
    private Double quantityGrams;
    private Integer calories;
    private Double proteinG;
    private Double carbsG;
    private Double fatG;
}
