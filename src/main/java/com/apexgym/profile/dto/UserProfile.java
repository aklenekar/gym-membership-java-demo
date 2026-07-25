package com.apexgym.profile.dto;

import lombok.Builder;

@Builder
public record UserProfile(
        Long id,
        String role,
        String email,
        String name,
        String goals,
        String level,
        String availability,
        String preferences,
        Integer age,
        String membershipPlan
) {
}