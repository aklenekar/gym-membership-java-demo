package com.apexgym.dto.admin;

import lombok.Builder;

@Builder
public record ClassAnalyticsDTO(
    Long totalClasses,
    Long totalBookings,
    Integer avgAttendancePercent,
    Integer cancellationRatePercent,
    Long waitlistCount
) {}
