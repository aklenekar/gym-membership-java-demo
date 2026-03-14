package com.apexgym.dto.admin;

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
