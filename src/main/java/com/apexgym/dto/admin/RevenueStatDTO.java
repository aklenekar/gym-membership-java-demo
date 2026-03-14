package com.apexgym.dto.admin;

import lombok.Builder;

@Builder
public record RevenueStatDTO(
    String label,
    Double amount,
    int heightPercent
) {}
