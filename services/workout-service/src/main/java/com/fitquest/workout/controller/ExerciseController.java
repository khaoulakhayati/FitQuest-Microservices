package com.fitquest.workout.controller;

import com.fitquest.workout.dto.ExerciseDto;
import com.fitquest.workout.mapper.ExerciseMapper;
import com.fitquest.workout.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/workouts/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseMapper exerciseMapper;

    @GetMapping
    public List<ExerciseDto> list() {
        return exerciseMapper.toDtoList(exerciseRepository.findAll());
    }
}
