package com.apexgym.staff.dto;

import lombok.Builder;
import java.time.LocalDate;

@Builder
public record TrainerCandidateDTO(
    Long userId,
    String firstName,
    String lastName,
    String email,
    String phone,
    String membershipPlan,
    String membershipStatus,
    LocalDate memberSince,
    LocalDate nextBillingDate,
    String gender,
    LocalDate dateOfBirth,
    Boolean isActive
) {}

