package com.apexgym.admin.dto;

import lombok.Builder;

@Builder
public record QuickStatsDTO(
    Long totalMembers,
    String membersTrend,
    Double monthlyRevenue,
    String revenueTrend,
    Long classesThisWeek,
    String classesTrend,
    Long activeTrainers,
    String trainersTrend
) {}
