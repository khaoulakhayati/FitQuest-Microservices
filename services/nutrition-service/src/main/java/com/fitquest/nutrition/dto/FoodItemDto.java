package com.fitquest.nutrition.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FoodItemDto {

    private Long id;
    private String name;
    private String brand;
    private String category;
    private Integer caloriesPer100g;
    private Double proteinPer100g;
    private Double carbsPer100g;
    private Double fatPer100g;
    private Integer defaultServingGrams;
}
