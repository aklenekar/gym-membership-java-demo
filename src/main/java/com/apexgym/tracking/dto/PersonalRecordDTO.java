package com.apexgym.tracking.dto;

import lombok.Builder;

@Builder
public record PersonalRecordDTO(
    String exercise,
    String value,
    String date,
    String icon
) {}
