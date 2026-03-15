package com.apexgym.profile.dto;

import lombok.Builder;

@Builder
public record HealthInfoDTO(
    String medicalConditions,
    String fitnessGoals
) {}
