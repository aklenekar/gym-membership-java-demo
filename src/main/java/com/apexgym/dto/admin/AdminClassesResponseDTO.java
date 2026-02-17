package com.apexgym.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminClassesResponseDTO {
    private List<AdminClassDTO> classes;
    private Long classesThisWeek;
    private Long totalBookings;
    private Integer avgCapacityPercent;
    private Long classesToday;
}
