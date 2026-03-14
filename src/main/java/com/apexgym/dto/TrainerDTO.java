package com.apexgym.dto;

import lombok.Builder;

@Builder
public record TrainerDTO(
    Long id,
    String fullName,
    String initials,
    String specialty,
    String bio,
    String certifications,
    Integer yearsExperience,
    Integer clientsTrained,
    Double rating,
    Boolean isHeadCoach,
    String imageUrl,
    String email,
    String phone
) {}