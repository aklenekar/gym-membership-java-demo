package com.apexgym.equipment.dto;

import java.util.List;

public record MaintenanceCalendarResponseDTO(
        List<MaintenanceRecordDTO> scheduledRecords,
        List<MaintenanceRecordDTO> overdueRecords
) {}