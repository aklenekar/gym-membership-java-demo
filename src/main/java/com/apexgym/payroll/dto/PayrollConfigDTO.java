package com.apexgym.payroll.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PayrollConfigDTO(
    Long id,
    Long trainerId,
    String trainerName,
    BigDecimal baseSalary,
    BigDecimal commissionRatePerClass,
    Double commissionPercentage,
    BigDecimal hourlyRate,
    String payFrequency
) {}
