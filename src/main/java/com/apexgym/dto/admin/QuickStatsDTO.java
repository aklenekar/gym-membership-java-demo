package com.apexgym.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuickStatsDTO {
    private Long totalMembers;
    private String membersTrend;      // "+12% this month"
    private Double monthlyRevenue;
    private String revenueTrend;      // "+8% this month"
    private Long classesThisWeek;
    private String classesTrend;      // "Same as last week"
    private Long activeTrainers;
    private String trainersTrend;     // "+2 new hires"
}
