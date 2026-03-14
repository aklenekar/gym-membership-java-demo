package com.apexgym.dto.admin;

import lombok.Builder;

@Builder
public record AdminClassDTO(
    Long id,
    String name,
    String category,
    String instructor,
    String location,
    String fullStartTime,
    String startDate,
    String startTime,
    Integer durationMinutes,
    Integer capacity,
    Integer bookedCount,
    String status
) {}
