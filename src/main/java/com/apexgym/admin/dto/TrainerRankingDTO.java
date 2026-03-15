package com.apexgym.admin.dto;

import lombok.Builder;

@Builder
public record TrainerRankingDTO(
    int rank,
    String name,
    Double rating,
    Long classCount,
    String imageUrl
) {}
