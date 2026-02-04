package com.apexgym.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipInfoDTO {
    private String plan;
    private String status;
    private LocalDate memberSince;
    private LocalDate nextBillingDate;
    private Double price;
}