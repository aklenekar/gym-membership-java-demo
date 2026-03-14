package com.apexgym.dto.admin;

import lombok.Builder;

@Builder
public record ClassRankingDTO(
    int rank,
    String className,
    Long bookings,
    int fillPercent
) {}
