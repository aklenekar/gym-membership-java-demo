package com.apexgym.payroll.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record CommissionRecordDTO(
    Long id,
    Long trainerId,
    String trainerName,
    String sessionTitle,
    LocalDate sessionDate,
    String sessionType,
    BigDecimal amount,
    String status,
    String notes
) {}
