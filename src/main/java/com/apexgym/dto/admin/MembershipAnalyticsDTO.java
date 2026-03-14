package com.apexgym.dto.admin;

import lombok.Builder;

@Builder
public record MembershipAnalyticsDTO(
    Long totalMembers,
    Long newMembers,
    Long renewals,
    Long cancellations,
    Double retentionRate
) {}
