package com.apexgym.equipment.dto;

import java.math.BigDecimal;

public record EquipmentStatsDTO(
        long totalEquipment,
        BigDecimal totalAssetValue,
        long upcomingMaintenanceCount,
        long overdueMaintenanceCount
) {}