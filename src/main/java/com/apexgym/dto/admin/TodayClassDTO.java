package com.apexgym.dto.admin;

import lombok.Builder;

@Builder
public record TodayClassDTO(
    Long id,
    String time,
    String name,
    String trainer,
    String capacity,
    String status
) {}
