package com.fitquest.nutrition.service;

import com.fitquest.nutrition.dto.MealItemRequest;
import com.fitquest.nutrition.entity.FoodItem;
import com.fitquest.nutrition.entity.Meal;
import com.fitquest.nutrition.entity.MealItem;
import com.fitquest.nutrition.exception.BadRequestException;
import com.fitquest.nutrition.exception.NotFoundException;
import com.fitquest.nutrition.repository.FoodItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NutritionCalculationService {

    private final FoodItemRepository foodItemRepository;

    public MealTotals buildMealItems(List<MealItemRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new BadRequestException("Meal must contain at least one food item");
        }

        Map<Long, FoodItem> foods = loadFoods(requests);
        List<MealItem> items = new ArrayList<>();
        int calories = 0;
        double protein = 0;
        double carbs = 0;
        double fat = 0;

        for (MealItemRequest request : requests) {
            FoodItem food = foods.get(request.getFoodItemId());
            if (food == null) {
                throw new NotFoundException("Food item not found: " + request.getFoodItemId());
            }
            double factor = request.getQuantityGrams() / 100.0;
            calories += Math.round(food.getCaloriesPer100g() * factor);
            protein += food.getProteinPer100g() * factor;
            carbs += food.getCarbsPer100g() * factor;
            fat += food.getFatPer100g() * factor;
            items.add(MealItem.builder()
                    .foodItemId(food.getId())
                    .quantityGrams(request.getQuantityGrams())
                    .build());
        }

        return new MealTotals(items, calories, round(protein), round(carbs), round(fat));
    }

    public Map<Long, FoodItem> loadFoodsForMeal(Meal meal) {
        List<Long> ids = meal.getItems().stream().map(MealItem::getFoodItemId).toList();
        return foodItemRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(FoodItem::getId, Function.identity()));
    }

    private Map<Long, FoodItem> loadFoods(List<MealItemRequest> requests) {
        List<Long> ids = requests.stream().map(MealItemRequest::getFoodItemId).distinct().toList();
        Map<Long, FoodItem> foods = foodItemRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(FoodItem::getId, Function.identity()));
        if (foods.size() != ids.size()) {
            throw new NotFoundException("One or more food items were not found");
        }
        return foods;
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    public record MealTotals(
            List<MealItem> items,
            int totalCalories,
            double totalProteinG,
            double totalCarbsG,
            double totalFatG
    ) {}
}
