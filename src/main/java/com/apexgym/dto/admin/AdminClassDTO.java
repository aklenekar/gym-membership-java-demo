package com.apexgym.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminClassDTO {
    private Long id;
    private String name;
    private String category;
    private String instructor;
    private String location;
    private String startTime;
    private Integer durationMinutes;
    private Integer capacity;
    private Integer bookedCount;
    private String status;         // AVAILABLE, ALMOST_FULL, FULL
}
