package com.apexgym.profile.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpgradeMembershipRequest(
        @NotBlank(message = "Plan is required")
        String plan
) {}