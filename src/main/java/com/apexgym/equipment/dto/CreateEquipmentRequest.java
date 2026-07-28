package com.apexgym.equipment.dto;

import com.apexgym.equipment.entity.enums.EquipmentCategory;
import com.apexgym.equipment.entity.enums.EquipmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateEquipmentRequest(
        @NotBlank(message = "Equipment name is required") String name,
        @NotNull(message = "Category is required") EquipmentCategory category,
        String brand,
        String model,
        String serialNumber,
        LocalDate purchaseDate,
        BigDecimal purchasePrice,
        String location,
        @NotNull(message = "Status is required") EquipmentStatus status,
        String imageUrl
) {}