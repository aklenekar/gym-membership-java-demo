package com.apexgym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerDTO {
    private Long id;
    private String fullName;
    private String initials;
    private String specialty;
    private String bio;
    private String certifications;
    private Integer yearsExperience;
    private Integer clientsTrained;
    private Double rating;
    private Boolean isHeadCoach;
    private String imageUrl;
    private String email;
    private String phone;
}