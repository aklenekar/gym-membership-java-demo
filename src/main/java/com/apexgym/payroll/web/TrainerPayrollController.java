package com.apexgym.payroll.web;

import com.apexgym.payroll.dto.*;
import com.apexgym.payroll.service.TrainerPayrollService;
import com.apexgym.shared.CommonHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payroll")
@RequiredArgsConstructor
public class TrainerPayrollController {

    private final TrainerPayrollService payrollService;
    private final CommonHelper commonHelper;

    @GetMapping("/summary")
    public ResponseEntity<TrainerPayrollSummaryDTO> getPayrollSummary() {
        return ResponseEntity.ok(payrollService.getPayrollSummary());
    }

    @GetMapping("/runs")
    public ResponseEntity<List<PayrollRunDTO>> getAllPayrollRuns() {
        return ResponseEntity.ok(payrollService.getAllPayrollRuns());
    }

    @GetMapping("/runs/trainer/{trainerId}")
    public ResponseEntity<List<PayrollRunDTO>> getPayrollRunsForTrainer(@PathVariable Long trainerId) {
        return ResponseEntity.ok(payrollService.getPayrollRunsForTrainer(trainerId));
    }

    @GetMapping("/me")
    public ResponseEntity<List<PayrollRunDTO>> getMyPayroll() {
        String email = commonHelper.getCurrentUserEmail();
        return ResponseEntity.ok(payrollService.getTrainerPayrollByEmail(email));
    }

    @GetMapping("/commissions/{trainerId}")
    public ResponseEntity<List<CommissionRecordDTO>> getCommissionsForTrainer(@PathVariable Long trainerId) {
        return ResponseEntity.ok(payrollService.getCommissionsForTrainer(trainerId));
    }

    @GetMapping("/config/{trainerId}")
    public ResponseEntity<PayrollConfigDTO> getTrainerConfig(@PathVariable Long trainerId) {
        return ResponseEntity.ok(payrollService.getTrainerConfig(trainerId));
    }

    @PostMapping("/config/{trainerId}")
    public ResponseEntity<PayrollConfigDTO> updateTrainerConfig(@PathVariable Long trainerId, @RequestBody PayrollConfigDTO dto) {
        return ResponseEntity.ok(payrollService.saveOrUpdateConfig(trainerId, dto));
    }

    @PostMapping("/generate")
    public ResponseEntity<PayrollRunDTO> generatePayrollRun(@RequestBody CreatePayrollRunRequestDTO request) {
        PayrollRunDTO created = payrollService.generatePayrollRun(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/runs/{id}/status")
    public ResponseEntity<PayrollRunDTO> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(payrollService.updatePayrollRunStatus(id, status));
    }

    @PostMapping("/commissions")
    public ResponseEntity<CommissionRecordDTO> logCommission(@RequestBody CommissionRecordDTO dto) {
        CommissionRecordDTO created = payrollService.logCommission(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
