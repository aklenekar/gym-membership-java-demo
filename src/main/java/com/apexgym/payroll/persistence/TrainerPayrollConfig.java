package com.apexgym.payroll.persistence;

import com.apexgym.staff.persistence.Trainer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "trainer_payroll_configs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerPayrollConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "trainer_id", nullable = false, unique = true)
    private Trainer trainer;

    @Column(name = "base_salary", precision = 10, scale = 2)
    private BigDecimal baseSalary;

    @Column(name = "commission_rate_per_class", precision = 10, scale = 2)
    private BigDecimal commissionRatePerClass;

    @Column(name = "commission_percentage")
    private Double commissionPercentage;

    @Column(name = "hourly_rate", precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "pay_frequency")
    @Builder.Default
    private String payFrequency = "MONTHLY";
}
