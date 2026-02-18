package com.apexgym.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentMemberDTO {
    private Long id;
    private String name;
    private String email;
    private String plan;
    private String joinedDate;        // "2 days ago"
    private String initials;
}
