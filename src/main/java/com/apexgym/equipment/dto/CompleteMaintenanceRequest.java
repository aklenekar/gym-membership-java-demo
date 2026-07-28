package com.apexgym.equipment.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CompleteMaintenanceRequest(
        @NotNull(message = "Completed date is required") LocalDate completedDate,
        BigDecimal cost,
        String notes
) {}