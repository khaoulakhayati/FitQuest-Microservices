package com.fitquest.workout.mapper;

import com.fitquest.workout.dto.ExerciseDto;
import com.fitquest.workout.entity.Exercise;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {

    ExerciseDto toDto(Exercise exercise);

    List<ExerciseDto> toDtoList(List<Exercise> exercises);
}
