package com.apexgym.tracking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record WorkoutRequest(
    @NotBlank(message = "Category is required")
    String category,
    @NotBlank(message = "Start Date is required")
    String startDate,
    @NotBlank(message = "Start Time is required")
    String startTime,
    @NotBlank(message = "Duration in Minutes is required")
    String durationMinutes,
    Integer caloriesBurned,
    String notes
) {}
