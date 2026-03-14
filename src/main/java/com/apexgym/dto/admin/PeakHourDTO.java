package com.apexgym.dto.admin;

import lombok.Builder;

@Builder
public record PeakHourDTO(
    String hour,
    int occupancyPercent
) {}
