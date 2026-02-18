package com.apexgym.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipDistributionDTO {
    private String plan;              // "STARTER", "PRO", "ELITE"
    private Long count;
    private Integer percentage;
}
