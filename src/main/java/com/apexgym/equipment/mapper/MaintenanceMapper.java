package com.apexgym.equipment.mapper;

import com.apexgym.equipment.dto.MaintenanceRecordDTO;
import com.apexgym.equipment.dto.ScheduleMaintenanceRequest;
import com.apexgym.equipment.entity.MaintenanceRecord;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MaintenanceMapper {

    @Mapping(target = "equipmentId", source = "equipment.id")
    @Mapping(target = "equipmentName", source = "equipment.name")
    MaintenanceRecordDTO toDto(MaintenanceRecord record);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "equipment", ignore = true)
    @Mapping(target = "status", constant = "SCHEDULED")
    @Mapping(target = "completedDate", ignore = true)
    MaintenanceRecord toEntity(ScheduleMaintenanceRequest request);
}