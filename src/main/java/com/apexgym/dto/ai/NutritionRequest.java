package com.apexgym.dto.ai;

import lombok.Data;

import java.util.List;

@Data
public class NutritionRequest {
    private String goal;
    private double weight;
    private int age;
    private String activityLevel;
    private List<String> dietaryRestrictions;
}
