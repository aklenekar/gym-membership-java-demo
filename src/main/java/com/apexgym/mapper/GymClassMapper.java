package com.apexgym.mapper;

import com.apexgym.dto.GymClassDTO;
import com.apexgym.entity.GymClass;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GymClassMapper {

    @Mapping(target = "instructor", source = "instructorName")
    @Mapping(target = "startTime", source = "classDate", dateFormat = "yyyy-MM-dd HH:mm")
    @Mapping(target = "durationMin", expression = "java(entity.getDurationMinutes() + \" mins\")")
    @Mapping(target = "capacity", expression = "java(String.valueOf(entity.getMaxCapacity()))")
    @Mapping(target = "booked", expression = "java(String.valueOf(entity.getCurrentBookings()))")
    @Mapping(target = "spotsInfo", expression = "java((entity.getMaxCapacity() - entity.getCurrentBookings()) + \" spots left\")")
    @Mapping(target = "category", source = "category")
    GymClassDTO toDTO(GymClass entity);
}
