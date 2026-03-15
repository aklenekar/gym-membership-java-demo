package com.apexgym.admin.dto;

import lombok.Builder;

@Builder
public record ClassAnalyticsDTO(
    Long totalClasses,
    Long totalBookings,
    Integer avgAttendancePercent,
    Integer cancellationRatePercent,
    Long waitlistCount
) {}
