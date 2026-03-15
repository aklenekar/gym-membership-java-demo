package com.apexgym.admin.dto;

import lombok.Builder;

@Builder
public record RecentMemberDTO(
    Long id,
    String name,
    String email,
    String plan,
    String joinedDate,
    String initials
) {}
