package com.apexgym.dto;

import lombok.Builder;

@Builder
public record PersonalRecordDTO(
    String exercise,
    String value,
    String date,
    String icon
) {}
