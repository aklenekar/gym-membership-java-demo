package com.apexgym.admin.dto;

import lombok.Builder;

@Builder
public record RevenueStatDTO(
    String label,
    Double amount,
    int heightPercent
) {}
