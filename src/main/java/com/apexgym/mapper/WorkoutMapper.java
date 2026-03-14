package com.apexgym.mapper;

import com.apexgym.dto.WorkoutDTO;
import com.apexgym.entity.WorkoutSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkoutMapper {

    @Mapping(target = "startTime", source = "startTime", dateFormat = "MMM d, h:mm a")
    @Mapping(target = "category", source = "category")
    WorkoutDTO toDTO(WorkoutSession session);
}
