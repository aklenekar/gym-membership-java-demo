package com.apexgym.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipAnalyticsDTO {
    private Long totalMembers;
    private Long newMembers;
    private Long renewals;
    private Long cancellations;
    private Double retentionRate;
}
