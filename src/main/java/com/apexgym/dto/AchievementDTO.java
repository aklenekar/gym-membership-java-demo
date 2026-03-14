package com.apexgym.dto;

import lombok.Builder;

@Builder
public record AchievementDTO(
    String name,
    String badge,
    String unlockedDate
) {}
