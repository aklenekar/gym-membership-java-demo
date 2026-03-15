package com.apexgym.admin.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record AdminDashboardResponseDTO(
    QuickStatsDTO quickStats,
    List<RecentMemberDTO> recentMembers,
    List<TodayClassDTO> todayClasses,
    List<RevenueStatDTO> revenueChart,
    List<MembershipDistributionDTO> membershipDistribution,
    List<TopTrainerDTO> topTrainers
) {}
