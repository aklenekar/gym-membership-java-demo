package com.apexgym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponseDTO {
    private List<GoalDTO> goals;
    private StatsDTO monthlyStats;
    private List<PersonalRecordDTO> personalRecords;
    private List<AchievementDTO> achievements;
}
