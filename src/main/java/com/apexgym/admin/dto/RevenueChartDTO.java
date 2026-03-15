package com.apexgym.admin.dto;

import lombok.Builder;

@Builder
public record RevenueChartDTO(
    String day,
    Double amount
) {}
