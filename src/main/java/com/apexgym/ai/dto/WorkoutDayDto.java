package com.apexgym.ai.dto;

import java.util.List;

public record WorkoutDayDto(
        String day,
        String focus,
        List<ExerciseDto> exercises,
        String duration,
        String rest
) {}

