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
public class DashboardResponseDTO {
    private MembershipInfoDTO membership;
    private StatsDTO stats;
    private List<UpcomingClassDTO> upcomingClasses;
    private List<ActivityDTO> recentActivities;
    private List<GoalDTO> goals;
}
