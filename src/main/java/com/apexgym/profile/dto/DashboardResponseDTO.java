package com.apexgym.profile.dto;

import com.apexgym.admin.dto.StatsDTO;
import com.apexgym.booking.dto.UpcomingClassDTO;
import com.apexgym.tracking.dto.GoalDTO;
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