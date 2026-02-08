package com.apexgym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GymClassDTO {
    private Long id;
    private String category;
    private String name;
    private String instructor;
    private String location;
    private String startTime;
    private String durationMin;
    private String capacity;
    private String booked;
    private String spotsInfo;
    private Boolean isBooked;
}
