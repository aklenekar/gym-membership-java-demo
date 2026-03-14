package com.apexgym.dto.admin;

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
