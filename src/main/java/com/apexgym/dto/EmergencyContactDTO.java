package com.apexgym.dto;

import lombok.Builder;

@Builder
public record EmergencyContactDTO(
    String name,
    String phone,
    String relationship
) {}
