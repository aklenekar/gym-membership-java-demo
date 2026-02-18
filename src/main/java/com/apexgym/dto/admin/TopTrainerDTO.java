package com.apexgym.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopTrainerDTO {
    private int rank;
    private String name;
    private String specialty;
    private Double rating;
    private String initials;
}
