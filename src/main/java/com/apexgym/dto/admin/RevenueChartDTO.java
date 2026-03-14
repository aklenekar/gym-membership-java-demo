package com.apexgym.dto.admin;

import lombok.Builder;

@Builder
public record RevenueChartDTO(
    String day,
    Double amount
) {}
