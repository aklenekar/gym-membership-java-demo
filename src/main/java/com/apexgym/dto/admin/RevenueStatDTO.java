package com.apexgym.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RevenueStatDTO {
    private String label;          // "Mon", "Tue" etc.
    private Double amount;
    private int heightPercent;
}
