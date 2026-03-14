package com.apexgym.dto.ai;

import lombok.Builder;
import java.util.List;

@Builder
public record WorkoutPlanRequest(
    String goals,
    int daysPerWeek,
    int experienceYears,
    List<String> availableEquipment
) {}
