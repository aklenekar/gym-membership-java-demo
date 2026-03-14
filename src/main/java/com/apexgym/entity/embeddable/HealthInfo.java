package com.apexgym.entity.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthInfo {
    @Column(name = "medical_conditions", length = 1000)
    private String medicalConditions;
    @Column(name = "fitness_goals", length = 1000)
    private String fitnessGoals;
}
