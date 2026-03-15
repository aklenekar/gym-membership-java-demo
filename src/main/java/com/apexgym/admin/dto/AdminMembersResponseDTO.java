package com.apexgym.admin.dto;

import lombok.Builder;
import java.util.List;

@Builder
public record AdminMembersResponseDTO(
    List<AdminMemberDTO> members,
    Long totalMembers,
    Long activeMembers,
    Long expiredMembers,
    Long newThisWeek,
    int currentPage,
    int totalPages
) {}
