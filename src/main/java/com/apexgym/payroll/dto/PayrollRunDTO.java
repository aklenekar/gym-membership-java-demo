package com.apexgym.payroll.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record PayrollRunDTO(
    Long id,
    Long trainerId,
    String trainerName,
    String trainerEmail,
    LocalDate periodStart,
    LocalDate periodEnd,
    BigDecimal baseSalaryAmount,
    BigDecimal commissionAmount,
    BigDecimal bonusAmount,
    BigDecimal deductionAmount,
    BigDecimal netPayout,
    String status,
    LocalDate paymentDate,
    String referenceNo
) {}
