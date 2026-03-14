package com.apexgym.dto;

import lombok.Builder;

@Builder
public record StatsDTO(
    Long workouts,
    Double hours,
    Long classes,
    Long caloriesBurned,
    Integer goalProgress
) {}