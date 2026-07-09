package com.fitquest.workout.mapper;

import com.fitquest.workout.dto.WorkoutSessionDto;
import com.fitquest.workout.entity.WorkoutSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkoutSessionMapper {

    @Mapping(target = "workoutId", source = "workout.id")
    @Mapping(target = "workoutName", source = "workout.name")
    WorkoutSessionDto toDto(WorkoutSession session);

    List<WorkoutSessionDto> toDtoList(List<WorkoutSession> sessions);
}
