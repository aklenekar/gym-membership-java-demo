package com.apexgym.equipment.dto;

import com.apexgym.equipment.entity.enums.MaintenanceStatus;
import com.apexgym.equipment.entity.enums.MaintenanceType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MaintenanceRecordDTO(
        Long id,
        Long equipmentId,
        String equipmentName,
        MaintenanceType type,
        LocalDate scheduledDate,
        LocalDate completedDate,
        String technician,
        BigDecimal cost,
        String notes,
        MaintenanceStatus status
) {}