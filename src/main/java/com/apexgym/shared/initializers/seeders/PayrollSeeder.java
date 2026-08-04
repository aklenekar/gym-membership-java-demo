package com.apexgym.shared.initializers.seeders;

import com.apexgym.payroll.persistence.*;
import com.apexgym.staff.persistence.Trainer;
import com.apexgym.staff.persistence.TrainerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PayrollSeeder {

    private final TrainerRepository trainerRepository;
    private final TrainerPayrollConfigRepository configRepository;
    private final TrainerCommissionRecordRepository commissionRepository;
    private final TrainerPayrollRunRepository payrollRunRepository;

    public void seed() {
        if (payrollRunRepository.count() > 0) {
            log.info("ℹ️ Payroll data already exists. Skipping PayrollSeeder.");
            return;
        }

        List<Trainer> trainers = trainerRepository.findAll();
        if (trainers.isEmpty()) {
            log.info("ℹ️ No trainers found. Skipping PayrollSeeder.");
            return;
        }

        log.info("🌱 Seeding initial payroll and commission data...");

        LocalDate now = LocalDate.now();
        LocalDate lastMonthStart = now.minusMonths(1).withDayOfMonth(1);
        LocalDate lastMonthEnd = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());

        for (int i = 0; i < trainers.size(); i++) {
            Trainer t = trainers.get(i);

            // 1. Seed Trainer Payroll Config
            TrainerPayrollConfig config = TrainerPayrollConfig.builder()
                    .trainer(t)
                    .baseSalary(new BigDecimal(3500 + (i * 300)))
                    .commissionRatePerClass(new BigDecimal(25 + (i * 5)))
                    .commissionPercentage(15.0)
                    .hourlyRate(new BigDecimal(40 + (i * 5)))
                    .payFrequency("MONTHLY")
                    .build();
            configRepository.save(config);

            // 2. Seed Pending Commissions
            TrainerCommissionRecord c1 = TrainerCommissionRecord.builder()
                    .trainer(t)
                    .sessionTitle("High-Intensity HIIT Bootcamp")
                    .sessionDate(now.minusDays(5))
                    .sessionType("GROUP_CLASS")
                    .amount(new BigDecimal("35.00"))
                    .status("PENDING")
                    .notes("Full class attendance bonus included")
                    .build();

            TrainerCommissionRecord c2 = TrainerCommissionRecord.builder()
                    .trainer(t)
                    .sessionTitle("Personal Training 1-on-1")
                    .sessionDate(now.minusDays(2))
                    .sessionType("PERSONAL_TRAINING")
                    .amount(new BigDecimal("50.00"))
                    .status("PENDING")
                    .notes("VIP Member Session")
                    .build();

            commissionRepository.saveAll(List.of(c1, c2));

            // 3. Seed Payroll Run
            BigDecimal base = config.getBaseSalary();
            BigDecimal comm = new BigDecimal("185.00");
            BigDecimal net = base.add(comm);

            TrainerPayrollRun run = TrainerPayrollRun.builder()
                    .trainer(t)
                    .periodStart(lastMonthStart)
                    .periodEnd(lastMonthEnd)
                    .baseSalaryAmount(base)
                    .commissionAmount(comm)
                    .bonusAmount(new BigDecimal("150.00"))
                    .deductionAmount(BigDecimal.ZERO)
                    .netPayout(net.add(new BigDecimal("150.00")))
                    .status(i == 0 ? "PAID" : "APPROVED")
                    .paymentDate(lastMonthEnd)
                    .referenceNo("PAY-REF-" + (202600 + i))
                    .build();

            payrollRunRepository.save(run);
        }

        log.info("✅ Payroll and commission data seeded successfully!");
    }
}
