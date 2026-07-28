package com.apexgym.equipment.dto;

import org.springframework.data.domain.Page;

public record EquipmentInventoryResponseDTO(
        Page<EquipmentDTO> equipmentPage,
        long totalEquipment,
        long operational,
        long underMaintenance,
        long outOfService
) {}