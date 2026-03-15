package com.apexgym.tracking.dto;

import com.apexgym.admin.dto.StatsDTO;
import lombok.Builder;
import java.util.List;

@Builder
public record WorkoutsResponseDTO(
    StatsDTO monthlyStats,
    List<WorkoutDTO> workouts
) {}