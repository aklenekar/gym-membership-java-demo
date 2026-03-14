package com.apexgym.dto;

import lombok.Builder;

@Builder
public record UserProfile(
    String goals,
    String level,
    String availability,
    String preferences,
    Integer age,
    String membershipPlan
) {}