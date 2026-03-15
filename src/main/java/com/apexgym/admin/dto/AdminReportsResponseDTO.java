package com.apexgym.admin.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record AdminReportsResponseDTO(
    Double totalRevenue,
    Double membershipRevenue,
    Double trainingRevenue,
    List<RevenueStatDTO> revenueChart,
    MembershipAnalyticsDTO membershipAnalytics,
    ClassAnalyticsDTO classAnalytics,
    List<ClassRankingDTO> popularClasses,
    List<TrainerRankingDTO> topTrainers,
    List<PeakHourDTO> peakHours
) {}
