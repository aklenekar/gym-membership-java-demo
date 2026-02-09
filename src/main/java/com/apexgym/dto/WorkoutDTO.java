package com.apexgym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDTO {
    private Long id;
    private String category;
    private String workoutType;
    private String startTime;
    private Integer durationMinutes;
    private Integer caloriesBurned;
    private String notes;
}