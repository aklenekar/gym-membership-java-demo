package com.apexgym.ai.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record NutritionRequest(
    String goal,
    double weight,
    int age,
    String activityLevel,
    List<String> dietaryRestrictions
) {}
