package com.apexgym.payroll.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record CreatePayrollRunRequestDTO(
    Long trainerId,
    LocalDate periodStart,
    LocalDate periodEnd,
    BigDecimal bonusAmount,
    BigDecimal deductionAmount,
    String referenceNo
) {}
