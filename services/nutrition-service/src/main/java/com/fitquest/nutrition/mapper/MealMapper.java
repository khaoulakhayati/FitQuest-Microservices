package com.fitquest.nutrition.mapper;

import com.fitquest.nutrition.dto.MealDto;
import com.fitquest.nutrition.entity.Meal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = MealItemMapper.class)
public interface MealMapper {

    @Mapping(target = "items", ignore = true)
    MealDto toDto(Meal meal);

    List<MealDto> toDtoList(List<Meal> meals);
}
