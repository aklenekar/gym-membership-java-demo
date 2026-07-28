package com.apexgym.equipment.dto;

import com.apexgym.equipment.entity.enums.EquipmentCategory;
import com.apexgym.equipment.entity.enums.EquipmentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EquipmentDTO(
        Long id,
        String name,
        EquipmentCategory category,
        String brand,
        String model,
        String serialNumber,
        LocalDate purchaseDate,
        BigDecimal purchasePrice,
        String location,
        EquipmentStatus status,
        LocalDate lastMaintenanceDate,
        LocalDate nextMaintenanceDate,
        String imageUrl
) {}
