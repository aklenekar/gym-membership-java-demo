package com.apexgym.dto.admin;

import lombok.Builder;

@Builder
public record MembershipDistributionDTO(
    String plan,
    Long count,
    Integer percentage
) {}
