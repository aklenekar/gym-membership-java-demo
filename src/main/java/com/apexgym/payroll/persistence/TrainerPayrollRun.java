package com.apexgym.payroll.persistence;

import com.apexgym.staff.persistence.Trainer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "trainer_payroll_runs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerPayrollRun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Column(name = "base_salary_amount", precision = 10, scale = 2)
    private BigDecimal baseSalaryAmount;

    @Column(name = "commission_amount", precision = 10, scale = 2)
    private BigDecimal commissionAmount;

    @Column(name = "bonus_amount", precision = 10, scale = 2)
    private BigDecimal bonusAmount;

    @Column(name = "deduction_amount", precision = 10, scale = 2)
    private BigDecimal deductionAmount;

    @Column(name = "net_payout", precision = 10, scale = 2, nullable = false)
    private BigDecimal netPayout;

    @Column(name = "status")
    @Builder.Default
    private String status = "DRAFT"; // DRAFT, APPROVED, PAID

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(name = "reference_no")
    private String referenceNo;
}
