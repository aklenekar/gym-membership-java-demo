package com.apexgym.dto;

import com.apexgym.admin.dto.StatsDTO;
import com.apexgym.booking.dto.UpcomingClassDTO;
import com.apexgym.profile.dto.MembershipInfoDTO;
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