package com.apexgym.dto;

import lombok.Builder;

@Builder
public record UserDTO(
    Long id,
    String email,
    String firstName,
    String lastName
) {}