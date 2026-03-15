package com.apexgym.admin.dto;

import lombok.Builder;

@Builder
public record AdminMemberDTO(
    Long id,
    String memberId,
    String fullName,
    String email,
    String plan,
    String status,
    String joinDate,
    String expiryDate,
    String initials
) {}