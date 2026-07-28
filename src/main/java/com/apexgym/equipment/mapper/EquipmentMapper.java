package com.apexgym.equipment.mapper;

import com.apexgym.equipment.dto.CreateEquipmentRequest;
import com.apexgym.equipment.dto.EquipmentDTO;
import com.apexgym.equipment.dto.UpdateEquipmentRequest;
import com.apexgym.equipment.entity.Equipment;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EquipmentMapper {

    EquipmentDTO toDto(Equipment equipment);

    Equipment toEntity(CreateEquipmentRequest request);

    void updateEntityFromRequest(UpdateEquipmentRequest request, @MappingTarget Equipment equipment);
}