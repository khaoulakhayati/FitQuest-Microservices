package com.fitquest.nutrition.service;

import com.fitquest.nutrition.dto.CreateMealRequest;
import com.fitquest.nutrition.dto.MealDto;
import com.fitquest.nutrition.dto.MealItemDto;
import com.fitquest.nutrition.dto.UpdateMealRequest;
import com.fitquest.nutrition.entity.FoodItem;
import com.fitquest.nutrition.entity.Meal;
import com.fitquest.nutrition.entity.MealItem;
import com.fitquest.nutrition.exception.NotFoundException;
import com.fitquest.nutrition.mapper.MealItemMapper;
import com.fitquest.nutrition.mapper.MealMapper;
import com.fitquest.nutrition.messaging.NutritionEventPublisher;
import com.fitquest.nutrition.repository.MealRepository;
import com.fitquest.nutrition.service.NutritionCalculationService.MealTotals;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MealService {

    private final MealRepository mealRepository;
    private final MealMapper mealMapper;
    private final MealItemMapper mealItemMapper;
    private final NutritionCalculationService calculationService;
    private final NutritionLogService nutritionLogService;
    private final NutritionEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<MealDto> findAll(Long userId) {
        return mealRepository.findByUserIdOrderByConsumedAtDesc(userId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public MealDto findById(Long userId, Long mealId) {
        return toDto(getMeal(userId, mealId));
    }

    @Transactional
    public MealDto create(Long userId, CreateMealRequest request) {
        Instant consumedAt = request.getConsumedAt() != null ? request.getConsumedAt() : Instant.now();
        MealTotals totals = calculationService.buildMealItems(request.getItems());

        Meal meal = Meal.builder()
                .userId(userId)
                .name(request.getName())
                .mealType(request.getMealType())
                .consumedAt(consumedAt)
                .items(new ArrayList<>(totals.items()))
                .totalCalories(totals.totalCalories())
                .totalProteinG(totals.totalProteinG())
                .totalCarbsG(totals.totalCarbsG())
                .totalFatG(totals.totalFatG())
                .build();

        Meal saved = mealRepository.save(meal);
        nutritionLogService.refreshDailyLog(userId, nutritionLogService.toLogDate(consumedAt));
        eventPublisher.publishMealLogged(userId, saved.getId(), saved.getTotalCalories());
        return toDto(saved);
    }

    @Transactional
    public MealDto update(Long userId, Long mealId, UpdateMealRequest request) {
        Meal meal = getMeal(userId, mealId);
        LocalDate oldDate = nutritionLogService.toLogDate(meal.getConsumedAt());

        Instant consumedAt = request.getConsumedAt() != null ? request.getConsumedAt() : meal.getConsumedAt();
        MealTotals totals = calculationService.buildMealItems(request.getItems());

        meal.setName(request.getName());
        meal.setMealType(request.getMealType());
        meal.setConsumedAt(consumedAt);
        meal.setItems(new ArrayList<>(totals.items()));
        meal.setTotalCalories(totals.totalCalories());
        meal.setTotalProteinG(totals.totalProteinG());
        meal.setTotalCarbsG(totals.totalCarbsG());
        meal.setTotalFatG(totals.totalFatG());

        Meal saved = mealRepository.save(meal);
        LocalDate newDate = nutritionLogService.toLogDate(consumedAt);
        nutritionLogService.refreshDailyLog(userId, oldDate);
        if (!oldDate.equals(newDate)) {
            nutritionLogService.refreshDailyLog(userId, newDate);
        }
        return toDto(saved);
    }

    @Transactional
    public void delete(Long userId, Long mealId) {
        Meal meal = getMeal(userId, mealId);
        LocalDate logDate = nutritionLogService.toLogDate(meal.getConsumedAt());
        mealRepository.delete(meal);
        nutritionLogService.refreshDailyLog(userId, logDate);
    }

    @Transactional(readOnly = true)
    public List<MealDto> findMealsForDate(Long userId, LocalDate date) {
        return mealRepository.findByUserIdOrderByConsumedAtDesc(userId).stream()
                .filter(m -> nutritionLogService.toLogDate(m.getConsumedAt()).equals(date))
                .map(this::toDto)
                .toList();
    }

    private Meal getMeal(Long userId, Long mealId) {
        return mealRepository.findByIdAndUserId(mealId, userId)
                .orElseThrow(() -> new NotFoundException("Meal not found: " + mealId));
    }

    private MealDto toDto(Meal meal) {
        Map<Long, FoodItem> foods = calculationService.loadFoodsForMeal(meal);
        List<MealItemDto> itemDtos = meal.getItems().stream()
                .map(item -> {
                    FoodItem food = foods.get(item.getFoodItemId());
                    return mealItemMapper.toDto(item, food);
                })
                .toList();

        MealDto dto = mealMapper.toDto(meal);
        dto.setItems(itemDtos);
        return dto;
    }
}
