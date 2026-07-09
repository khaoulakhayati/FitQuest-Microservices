package com.fitquest.nutrition.mapper;

import com.fitquest.nutrition.dto.MealItemDto;
import com.fitquest.nutrition.entity.FoodItem;
import com.fitquest.nutrition.entity.MealItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MealItemMapper {

    @Mapping(target = "foodName", source = "foodItem.name")
    @Mapping(target = "calories", expression = "java(calculateCalories(foodItem, mealItem.getQuantityGrams()))")
    @Mapping(target = "proteinG", expression = "java(calculateMacro(foodItem.getProteinPer100g(), mealItem.getQuantityGrams()))")
    @Mapping(target = "carbsG", expression = "java(calculateMacro(foodItem.getCarbsPer100g(), mealItem.getQuantityGrams()))")
    @Mapping(target = "fatG", expression = "java(calculateMacro(foodItem.getFatPer100g(), mealItem.getQuantityGrams()))")
    MealItemDto toDto(MealItem mealItem, FoodItem foodItem);

    default Integer calculateCalories(FoodItem foodItem, Double quantityGrams) {
        return (int) Math.round(foodItem.getCaloriesPer100g() * quantityGrams / 100.0);
    }

    default Double calculateMacro(Double per100g, Double quantityGrams) {
        return Math.round(per100g * quantityGrams / 100.0 * 10.0) / 10.0;
    }
}
