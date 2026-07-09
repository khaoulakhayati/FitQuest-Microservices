package com.fitquest.nutrition.dto;

import com.fitquest.nutrition.entity.MealType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class CreateMealRequest {

    @NotBlank
    private String name;

    @NotNull
    private MealType mealType;

    private Instant consumedAt;

    @NotEmpty
    @Valid
    private List<MealItemRequest> items;
}
