package com.apexgym.admin.dto;

import lombok.Builder;

@Builder
public record ClassRankingDTO(
    int rank,
    String className,
    Long bookings,
    int fillPercent
) {}
