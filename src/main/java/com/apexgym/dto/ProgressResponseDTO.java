package com.apexgym.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record ProgressResponseDTO(
    List<GoalDTO> goals,
    StatsDTO monthlyStats,
    List<PersonalRecordDTO> personalRecords,
    List<AchievementDTO> achievements
) {}
