package com.fitquest.nutrition.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class MealItemRequest {

    @NotNull
    private Long foodItemId;

    @NotNull
    @Positive
    private Double quantityGrams;
}
