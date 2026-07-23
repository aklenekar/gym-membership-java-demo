package com.apexgym.staff.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record TrainerClassDTO(
    Long id,
    String name,
    String category,
    String location,
    LocalDateTime classDate,
    Integer durationMinutes,
    Boolean isActive,
    Integer capacity,
    Integer bookedCount,
    String fullStartTime,
    String status
) {}

