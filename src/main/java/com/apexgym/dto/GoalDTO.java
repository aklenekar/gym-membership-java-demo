package com.apexgym.dto;

import lombok.Builder;

@Builder
public record GoalDTO(
    Long id,
    String name,
    Integer currentValue,
    Integer targetValue,
    Integer progressPercentage
) {}
