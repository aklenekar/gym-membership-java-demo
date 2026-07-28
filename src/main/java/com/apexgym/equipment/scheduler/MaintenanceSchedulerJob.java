package com.apexgym.equipment.scheduler;

import com.apexgym.equipment.entity.MaintenanceRecord;
import com.apexgym.equipment.entity.MaintenanceSchedule;
import com.apexgym.equipment.entity.enums.MaintenanceStatus;
import com.apexgym.equipment.entity.enums.MaintenanceType;
import com.apexgym.equipment.persistence.MaintenanceRecordRepository;
import com.apexgym.equipment.persistence.MaintenanceScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaintenanceSchedulerJob {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final MaintenanceScheduleRepository maintenanceScheduleRepository;

    /**
     * Executes daily at 01:00 AM.
     * 1. Transitions uncompleted records past scheduledDate to OVERDUE.
     * 2. Generates new MaintenanceRecord entries for due recurring schedules.
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void processMaintenanceSchedules() {
        LocalDate today = LocalDate.now();
        log.info("Starting maintenance scheduler job for date: {}", today);

        // Step 1: Mark uncompleted past-due records as OVERDUE
        List<MaintenanceRecord> overdueRecords = maintenanceRecordRepository.findOverdue(today);
        for (MaintenanceRecord record : overdueRecords) {
            record.setStatus(MaintenanceStatus.OVERDUE);
        }
        maintenanceRecordRepository.saveAll(overdueRecords);
        log.info("Updated {} records to OVERDUE", overdueRecords.size());

        // Step 2: Auto-generate new records from active recurring rules
        List<MaintenanceSchedule> dueSchedules = maintenanceScheduleRepository.findDueForGeneration(today);
        for (MaintenanceSchedule schedule : dueSchedules) {
            LocalDate nextScheduledDate = calculateNextDate(today, schedule.getFrequency());

            MaintenanceRecord newRecord = MaintenanceRecord.builder()
                    .equipment(schedule.getEquipment())
                    .type(MaintenanceType.ROUTINE)
                    .scheduledDate(nextScheduledDate)
                    .status(MaintenanceStatus.SCHEDULED)
                    .notes("Auto-generated recurring routine maintenance")
                    .build();

            maintenanceRecordRepository.save(newRecord);

            schedule.setLastGeneratedDate(today);
            maintenanceScheduleRepository.save(schedule);
        }
        log.info("Generated {} new recurring maintenance records", dueSchedules.size());
    }

    private LocalDate calculateNextDate(LocalDate baseDate, com.apexgym.equipment.entity.enums.MaintenanceFrequency frequency) {
        return switch (frequency) {
            case WEEKLY -> baseDate.plusWeeks(1);
            case MONTHLY -> baseDate.plusMonths(1);
            case QUARTERLY -> baseDate.plusMonths(3);
            case ANNUAL -> baseDate.plusYears(1);
        };
    }
}