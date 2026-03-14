package com.apexgym.dto.admin;

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
