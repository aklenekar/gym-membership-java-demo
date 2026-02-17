package com.apexgym.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminMemberDTO {
    private Long id;
    private String memberId;       // #M-1001
    private String fullName;
    private String email;
    private String plan;
    private String status;
    private String joinDate;
    private String expiryDate;
    private String initials;
}