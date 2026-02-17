package com.apexgym.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassAnalyticsDTO {
    private Long totalClasses;
    private Long totalBookings;
    private Integer avgAttendancePercent;
    private Integer cancellationRatePercent;
    private Long waitlistCount;
}
