package com.fitquest.nutrition.controller;

import com.fitquest.nutrition.dto.CreateMealRequest;
import com.fitquest.nutrition.dto.MealDto;
import com.fitquest.nutrition.dto.UpdateMealRequest;
import com.fitquest.nutrition.service.MealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nutrition/meals")
@RequiredArgsConstructor
@Tag(name = "Meals")
public class MealController {

    private final MealService mealService;

    @GetMapping
    @Operation(summary = "List all meals for the current user")
    public List<MealDto> list(@RequestHeader("X-User-Id") Long userId) {
        return mealService.findAll(userId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a meal by ID")
    public MealDto get(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        return mealService.findById(userId, id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new meal")
    public MealDto create(@RequestHeader("X-User-Id") Long userId,
                          @Valid @RequestBody CreateMealRequest request) {
        return mealService.create(userId, request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing meal")
    public MealDto update(@RequestHeader("X-User-Id") Long userId,
                          @PathVariable Long id,
                          @Valid @RequestBody UpdateMealRequest request) {
        return mealService.update(userId, id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a meal")
    public void delete(@RequestHeader("X-User-Id") Long userId, @PathVariable Long id) {
        mealService.delete(userId, id);
    }
}
