package com.apexgym.dto;

import lombok.Builder;

@Builder
public record PersonalInfoDTO(
    String firstName,
    String lastName,
    String email,
    String phone,
    String dateOfBirth,
    String gender
) {}
