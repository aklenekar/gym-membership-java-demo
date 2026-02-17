package com.apexgym.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReportsResponseDTO {
    private Double totalRevenue;
    private Double membershipRevenue;
    private Double trainingRevenue;
    private List<RevenueStatDTO> revenueChart;
    private MembershipAnalyticsDTO membershipAnalytics;
    private ClassAnalyticsDTO classAnalytics;
    private List<ClassRankingDTO> popularClasses;
    private List<TrainerRankingDTO> topTrainers;
    private List<PeakHourDTO> peakHours;
}
