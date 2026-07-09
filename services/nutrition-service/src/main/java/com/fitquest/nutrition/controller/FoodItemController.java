package com.fitquest.nutrition.controller;

import com.fitquest.nutrition.dto.FoodItemDto;
import com.fitquest.nutrition.mapper.FoodItemMapper;
import com.fitquest.nutrition.repository.FoodItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/nutrition/foods")
@RequiredArgsConstructor
public class FoodItemController {

    private final FoodItemRepository foodItemRepository;
    private final FoodItemMapper foodItemMapper;

    @GetMapping
    public List<FoodItemDto> list() {
        return foodItemMapper.toDtoList(foodItemRepository.findAll());
    }
}
