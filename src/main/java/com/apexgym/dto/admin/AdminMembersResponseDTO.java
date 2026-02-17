package com.apexgym.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminMembersResponseDTO {
    private List<AdminMemberDTO> members;
    private Long totalMembers;
    private Long activeMembers;
    private Long expiredMembers;
    private Long newThisWeek;
    private int currentPage;
    private int totalPages;
}
