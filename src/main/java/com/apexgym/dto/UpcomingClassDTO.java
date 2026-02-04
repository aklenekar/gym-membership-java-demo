package com.apexgym.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpcomingClassDTO {
    private Long id;
    private String name;
    private String instructor;
    private String location;
    private String time;
    private String date;
    private Boolean isBooked;
}
