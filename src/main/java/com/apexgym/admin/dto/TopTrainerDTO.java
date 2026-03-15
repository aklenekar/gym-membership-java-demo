package com.apexgym.admin.dto;

import lombok.Builder;

@Builder
public record TopTrainerDTO(
    int rank,
    String name,
    String specialty,
    Double rating,
    String initials,
    String imageUrl
) {}
