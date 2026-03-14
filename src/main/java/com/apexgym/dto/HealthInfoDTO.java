package com.apexgym.dto;

import lombok.Builder;

@Builder
public record HealthInfoDTO(
    String medicalConditions,
    String fitnessGoals
) {}
