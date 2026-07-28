package com.apexgym.equipment.dto;

import com.apexgym.equipment.entity.enums.MaintenanceType;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ScheduleMaintenanceRequest(
        @NotNull(message = "Equipment ID is required") Long equipmentId,
        @NotNull(message = "Maintenance type is required") MaintenanceType type,
        @NotNull(message = "Scheduled date is required") LocalDate scheduledDate,
        String technician,
        String notes
) {}