package com.apexgym.tracking.dto;

import lombok.Builder;

@Builder
public record AchievementDTO(
    String name,
    String badge,
    String unlockedDate
) {}
