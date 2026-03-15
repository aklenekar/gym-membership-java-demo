package com.apexgym.admin.dto;

import lombok.Builder;

@Builder
public record PeakHourDTO(
    String hour,
    int occupancyPercent
) {}
