package com.apexgym.admin.dto;

import lombok.Builder;

@Builder
public record MembershipDistributionDTO(
    String plan,
    Long count,
    Integer percentage
) {}
