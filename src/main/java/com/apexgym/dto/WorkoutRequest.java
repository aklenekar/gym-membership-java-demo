package com.apexgym.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRequest {
    @NotBlank(message = "Category is required")
    private String category;
    @NotBlank(message = "Start Date is required")
    private String startDate;
    @NotBlank(message = "Start Time is required")
    private String startTime;
    @NotBlank(message = "Duration in Minutes is required")
    private String durationMinutes;
    private Integer caloriesBurned;
    private String notes;
}
