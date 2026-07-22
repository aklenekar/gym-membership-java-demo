package com.apexgym.staff.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record TrainerClassesResponseDTO(
    List<TrainerClassDTO> classes,
    Long totalClasses,
    Long upcomingClasses,
    Long completedClasses,
    Integer avgCapacityUtilization
) {}

