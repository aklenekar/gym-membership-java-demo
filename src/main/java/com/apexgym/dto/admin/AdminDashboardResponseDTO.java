package com.apexgym.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

// ============================================================
// ADMIN DASHBOARD
// ============================================================

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardResponseDTO {
    private QuickStatsDTO quickStats;
    private List<RecentMemberDTO> recentMembers;
    private List<TodayClassDTO> todayClasses;
    private List<RevenueChartDTO> revenueChart;
    private List<MembershipDistributionDTO> membershipDistribution;
    private List<TopTrainerDTO> topTrainers;
}

