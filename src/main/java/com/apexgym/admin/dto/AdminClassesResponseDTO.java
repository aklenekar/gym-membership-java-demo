package com.apexgym.admin.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record AdminClassesResponseDTO(
    List<AdminClassDTO> classes,
    Long classesThisWeek,
    Long totalBookings,
    Integer avgCapacityPercent,
    Long classesToday
) {}
