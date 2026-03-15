package com.apexgym.profile.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record MembershipInfoDTO(
    String plan,
    String status,
    LocalDate memberSince,
    LocalDate nextBillingDate,
    Double price
) {}