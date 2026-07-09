package com.fitquest.workout.mapper;

import com.fitquest.workout.dto.WorkoutDto;
import com.fitquest.workout.entity.Workout;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = ExerciseMapper.class)
public interface WorkoutMapper {

    @Mapping(target = "exercises", source = "exercises")
    WorkoutDto toDto(Workout workout);

    List<WorkoutDto> toDtoList(List<Workout> workouts);
}
