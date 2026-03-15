package com.apexgym.profile.dto;

import lombok.Builder;

@Builder
public record ActivityDTO(
    Long id,
    String icon,
    String title,
    String time
) {}
