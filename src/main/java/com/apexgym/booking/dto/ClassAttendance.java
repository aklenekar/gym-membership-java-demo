package com.apexgym.booking.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record ClassAttendance(
    String className,
    String category,
    String instructor,
    LocalDateTime attendedAt
) {}
