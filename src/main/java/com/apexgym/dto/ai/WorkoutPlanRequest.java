package com.apexgym.dto.ai;

import lombok.Data;

import java.util.List;

@Data
public class WorkoutPlanRequest {
    private String goals;
    private int daysPerWeek;
    private int experienceYears;
    private List<String> availableEquipment;
}
