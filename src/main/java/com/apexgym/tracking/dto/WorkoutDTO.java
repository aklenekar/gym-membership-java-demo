package com.apexgym.tracking.dto;

import lombok.Builder;

@Builder
public record WorkoutDTO(
    Long id,
    String category,
    String workoutType,
    String startTime,
    Integer durationMinutes,
    Integer caloriesBurned,
    String notes
) {}