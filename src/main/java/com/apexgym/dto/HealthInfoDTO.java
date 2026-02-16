package com.apexgym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthInfoDTO {
    private String medicalConditions;
    private String fitnessGoals;
}
