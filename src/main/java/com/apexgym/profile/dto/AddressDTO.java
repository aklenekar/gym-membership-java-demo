package com.apexgym.profile.dto;

import lombok.Builder;

@Builder
public record AddressDTO(
    String street,
    String city,
    String state,
    String zipCode,
    String country
) {}
