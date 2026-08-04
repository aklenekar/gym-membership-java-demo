package com.apexgym.payroll.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record TrainerPayrollSummaryDTO(
    BigDecimal totalMonthlyPayroll,
    BigDecimal totalPendingCommissions,
    BigDecimal totalPaidThisMonth,
    Integer activeTrainersCount,
    List<PayrollRunDTO> recentPayrollRuns,
    List<CommissionRecordDTO> pendingCommissions
) {}
