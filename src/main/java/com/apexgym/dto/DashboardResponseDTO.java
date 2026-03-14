package com.apexgym.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record DashboardResponseDTO(
    MembershipInfoDTO membership,
    StatsDTO stats,
    List<UpcomingClassDTO> upcomingClasses,
    List<ActivityDTO> recentActivities,
    List<GoalDTO> goals
) {}
