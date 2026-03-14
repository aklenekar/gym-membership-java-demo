package com.apexgym.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record WorkoutsResponseDTO(
    StatsDTO monthlyStats,
    List<WorkoutDTO> workouts
) {}