package com.fitquest.nutrition.mapper;

import com.fitquest.nutrition.dto.FoodItemDto;
import com.fitquest.nutrition.entity.FoodItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FoodItemMapper {

    FoodItemDto toDto(FoodItem foodItem);

    List<FoodItemDto> toDtoList(List<FoodItem> foodItems);
}
