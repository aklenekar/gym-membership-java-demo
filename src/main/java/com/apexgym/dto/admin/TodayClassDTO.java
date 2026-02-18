package com.apexgym.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodayClassDTO {
    private Long id;
    private String time;              // "6:00 AM"
    private String name;
    private String trainer;
    private String capacity;          // "18/20"
    private String status;            // "AVAILABLE", "FULL"
}
