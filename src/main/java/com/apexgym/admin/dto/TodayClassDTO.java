package com.apexgym.admin.dto;

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
