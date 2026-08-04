package com.apexgym.payroll.service;

import com.apexgym.payroll.dto.*;
import com.apexgym.payroll.persistence.*;
import com.apexgym.staff.persistence.Trainer;
import com.apexgym.staff.persistence.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainerPayrollService {

    private final TrainerRepository trainerRepository;
    private final TrainerPayrollConfigRepository configRepository;
    private final TrainerCommissionRecordRepository commissionRepository;
    private final TrainerPayrollRunRepository payrollRunRepository;

    @Transactional(readOnly = true)
    public TrainerPayrollSummaryDTO getPayrollSummary() {
        List<TrainerPayrollRun> allRuns = payrollRunRepository.findAll();
        List<TrainerCommissionRecord> pendingCommissions = commissionRepository.findByStatus("PENDING");

        BigDecimal totalMonthlyPayroll = allRuns.stream()
                .filter(r -> "PAID".equalsIgnoreCase(r.getStatus()) || "APPROVED".equalsIgnoreCase(r.getStatus()))
                .map(TrainerPayrollRun::getNetPayout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPendingCommissions = pendingCommissions.stream()
                .map(TrainerCommissionRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalPaidThisMonth = allRuns.stream()
                .filter(r -> "PAID".equalsIgnoreCase(r.getStatus()))
                .map(TrainerPayrollRun::getNetPayout)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long activeTrainersCount = trainerRepository.countByIsActiveTrue();

        List<PayrollRunDTO> runDTOs = allRuns.stream()
                .map(this::mapToPayrollRunDTO)
                .collect(Collectors.toList());

        List<CommissionRecordDTO> commissionDTOs = pendingCommissions.stream()
                .map(this::mapToCommissionRecordDTO)
                .collect(Collectors.toList());

        return TrainerPayrollSummaryDTO.builder()
                .totalMonthlyPayroll(totalMonthlyPayroll)
                .totalPendingCommissions(totalPendingCommissions)
                .totalPaidThisMonth(totalPaidThisMonth)
                .activeTrainersCount((int) activeTrainersCount)
                .recentPayrollRuns(runDTOs)
                .pendingCommissions(commissionDTOs)
                .build();
    }

    @Transactional(readOnly = true)
    public List<PayrollRunDTO> getAllPayrollRuns() {
        return payrollRunRepository.findAll().stream()
                .map(this::mapToPayrollRunDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PayrollRunDTO> getPayrollRunsForTrainer(Long trainerId) {
        return payrollRunRepository.findByTrainerId(trainerId).stream()
                .map(this::mapToPayrollRunDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PayrollRunDTO> getTrainerPayrollByEmail(String email) {
        Optional<Trainer> trainerOpt = trainerRepository.findByUserEmail(email);
        if (trainerOpt.isEmpty()) {
            return List.of();
        }
        return payrollRunRepository.findByTrainerId(trainerOpt.get().getId()).stream()
                .map(this::mapToPayrollRunDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommissionRecordDTO> getCommissionsForTrainer(Long trainerId) {
        return commissionRepository.findByTrainerId(trainerId).stream()
                .map(this::mapToCommissionRecordDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PayrollConfigDTO getTrainerConfig(Long trainerId) {
        TrainerPayrollConfig config = configRepository.findByTrainerId(trainerId)
                .orElseGet(() -> {
                    Trainer trainer = trainerRepository.findById(trainerId)
                            .orElseThrow(() -> new IllegalArgumentException("Trainer not found with ID: " + trainerId));
                    return TrainerPayrollConfig.builder()
                            .trainer(trainer)
                            .baseSalary(new BigDecimal("3500.00"))
                            .commissionRatePerClass(new BigDecimal("25.00"))
                            .commissionPercentage(15.0)
                            .hourlyRate(new BigDecimal("40.00"))
                            .payFrequency("MONTHLY")
                            .build();
                });
        return mapToConfigDTO(config);
    }

    @Transactional
    public PayrollConfigDTO saveOrUpdateConfig(Long trainerId, PayrollConfigDTO dto) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with ID: " + trainerId));

        TrainerPayrollConfig config = configRepository.findByTrainerId(trainerId)
                .orElseGet(() -> TrainerPayrollConfig.builder().trainer(trainer).build());

        config.setBaseSalary(dto.baseSalary() != null ? dto.baseSalary() : BigDecimal.ZERO);
        config.setCommissionRatePerClass(dto.commissionRatePerClass() != null ? dto.commissionRatePerClass() : BigDecimal.ZERO);
        config.setCommissionPercentage(dto.commissionPercentage() != null ? dto.commissionPercentage() : 0.0);
        config.setHourlyRate(dto.hourlyRate() != null ? dto.hourlyRate() : BigDecimal.ZERO);
        config.setPayFrequency(dto.payFrequency() != null ? dto.payFrequency() : "MONTHLY");

        TrainerPayrollConfig saved = configRepository.save(config);
        return mapToConfigDTO(saved);
    }

    @Transactional
    public PayrollRunDTO generatePayrollRun(CreatePayrollRunRequestDTO request) {
        Trainer trainer = trainerRepository.findById(request.trainerId())
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with ID: " + request.trainerId()));

        TrainerPayrollConfig config = configRepository.findByTrainerId(request.trainerId())
                .orElseGet(() -> TrainerPayrollConfig.builder()
                        .trainer(trainer)
                        .baseSalary(new BigDecimal("3500.00"))
                        .commissionRatePerClass(new BigDecimal("25.00"))
                        .build());

        BigDecimal baseSalary = config.getBaseSalary() != null ? config.getBaseSalary() : BigDecimal.ZERO;

        BigDecimal commissions = commissionRepository.sumCommissionByTrainerAndDateRange(
                request.trainerId(), request.periodStart(), request.periodEnd()
        );
        if (commissions == null) {
            commissions = BigDecimal.ZERO;
        }

        BigDecimal bonus = request.bonusAmount() != null ? request.bonusAmount() : BigDecimal.ZERO;
        BigDecimal deduction = request.deductionAmount() != null ? request.deductionAmount() : BigDecimal.ZERO;

        BigDecimal netPayout = baseSalary.add(commissions).add(bonus).subtract(deduction);

        TrainerPayrollRun run = TrainerPayrollRun.builder()
                .trainer(trainer)
                .periodStart(request.periodStart())
                .periodEnd(request.periodEnd())
                .baseSalaryAmount(baseSalary)
                .commissionAmount(commissions)
                .bonusAmount(bonus)
                .deductionAmount(deduction)
                .netPayout(netPayout)
                .status("APPROVED")
                .paymentDate(LocalDate.now())
                .referenceNo(request.referenceNo() != null ? request.referenceNo() : "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .build();

        TrainerPayrollRun saved = payrollRunRepository.save(run);
        return mapToPayrollRunDTO(saved);
    }

    @Transactional
    public PayrollRunDTO updatePayrollRunStatus(Long runId, String status) {
        TrainerPayrollRun run = payrollRunRepository.findById(runId)
                .orElseThrow(() -> new IllegalArgumentException("Payroll run not found with ID: " + runId));

        run.setStatus(status.toUpperCase());
        if ("PAID".equalsIgnoreCase(status) && run.getPaymentDate() == null) {
            run.setPaymentDate(LocalDate.now());
        }

        TrainerPayrollRun updated = payrollRunRepository.save(run);
        return mapToPayrollRunDTO(updated);
    }

    @Transactional
    public CommissionRecordDTO logCommission(CommissionRecordDTO dto) {
        Trainer trainer = trainerRepository.findById(dto.trainerId())
                .orElseThrow(() -> new IllegalArgumentException("Trainer not found with ID: " + dto.trainerId()));

        TrainerCommissionRecord record = TrainerCommissionRecord.builder()
                .trainer(trainer)
                .sessionTitle(dto.sessionTitle())
                .sessionDate(dto.sessionDate() != null ? dto.sessionDate() : LocalDate.now())
                .sessionType(dto.sessionType() != null ? dto.sessionType() : "GROUP_CLASS")
                .amount(dto.amount())
                .status(dto.status() != null ? dto.status() : "PENDING")
                .notes(dto.notes())
                .build();

        TrainerCommissionRecord saved = commissionRepository.save(record);
        return mapToCommissionRecordDTO(saved);
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void seedInitialPayrollData() {
        if (payrollRunRepository.count() > 0) {
            return;
        }

        List<Trainer> trainers = trainerRepository.findAll();
        if (trainers.isEmpty()) {
            return;
        }

        LocalDate now = LocalDate.now();
        LocalDate lastMonthStart = now.minusMonths(1).withDayOfMonth(1);
        LocalDate lastMonthEnd = now.minusMonths(1).withDayOfMonth(now.minusMonths(1).lengthOfMonth());

        for (int i = 0; i < trainers.size(); i++) {
            Trainer t = trainers.get(i);

            TrainerPayrollConfig config = TrainerPayrollConfig.builder()
                    .trainer(t)
                    .baseSalary(new BigDecimal(3200 + (i * 300)))
                    .commissionRatePerClass(new BigDecimal(25 + (i * 5)))
                    .commissionPercentage(15.0)
                    .hourlyRate(new BigDecimal(40 + (i * 5)))
                    .payFrequency("MONTHLY")
                    .build();
            configRepository.save(config);

            // Log demo commissions
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

            // Log demo payroll run
            BigDecimal base = config.getBaseSalary();
            BigDecimal comm = new BigDecimal("180.00");
            BigDecimal net = base.add(comm);

            TrainerPayrollRun run = TrainerPayrollRun.builder()
                    .trainer(t)
                    .periodStart(lastMonthStart)
                    .periodEnd(lastMonthEnd)
                    .baseSalaryAmount(base)
                    .commissionAmount(comm)
                    .bonusAmount(new BigDecimal("100.00"))
                    .deductionAmount(BigDecimal.ZERO)
                    .netPayout(net.add(new BigDecimal("100.00")))
                    .status(i == 0 ? "PAID" : "APPROVED")
                    .paymentDate(lastMonthEnd)
                    .referenceNo("PAY-REF-" + (1000 + i))
                    .build();

            payrollRunRepository.save(run);
        }
    }

    private PayrollRunDTO mapToPayrollRunDTO(TrainerPayrollRun run) {
        return PayrollRunDTO.builder()
                .id(run.getId())
                .trainerId(run.getTrainer().getId())
                .trainerName(run.getTrainer().getFullName())
                .trainerEmail(run.getTrainer().getEmail())
                .periodStart(run.getPeriodStart())
                .periodEnd(run.getPeriodEnd())
                .baseSalaryAmount(run.getBaseSalaryAmount())
                .commissionAmount(run.getCommissionAmount())
                .bonusAmount(run.getBonusAmount())
                .deductionAmount(run.getDeductionAmount())
                .netPayout(run.getNetPayout())
                .status(run.getStatus())
                .paymentDate(run.getPaymentDate())
                .referenceNo(run.getReferenceNo())
                .build();
    }

    private CommissionRecordDTO mapToCommissionRecordDTO(TrainerCommissionRecord record) {
        return CommissionRecordDTO.builder()
                .id(record.getId())
                .trainerId(record.getTrainer().getId())
                .trainerName(record.getTrainer().getFullName())
                .sessionTitle(record.getSessionTitle())
                .sessionDate(record.getSessionDate())
                .sessionType(record.getSessionType())
                .amount(record.getAmount())
                .status(record.getStatus())
                .notes(record.getNotes())
                .build();
    }

    private PayrollConfigDTO mapToConfigDTO(TrainerPayrollConfig config) {
        return PayrollConfigDTO.builder()
                .id(config.getId())
                .trainerId(config.getTrainer().getId())
                .trainerName(config.getTrainer().getFullName())
                .baseSalary(config.getBaseSalary())
                .commissionRatePerClass(config.getCommissionRatePerClass())
                .commissionPercentage(config.getCommissionPercentage())
                .hourlyRate(config.getHourlyRate())
                .payFrequency(config.getPayFrequency())
                .build();
    }
}
