package com.apexgym.shared.initializers.seeders;

import com.apexgym.equipment.entity.Equipment;
import com.apexgym.equipment.entity.MaintenanceRecord;
import com.apexgym.equipment.entity.enums.MaintenanceStatus;
import com.apexgym.equipment.entity.enums.MaintenanceType;
import com.apexgym.equipment.persistence.EquipmentRepository;
import com.apexgym.equipment.persistence.MaintenanceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MaintenanceSeeder {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final EquipmentRepository equipmentRepository;


    public void seed() {
        if (maintenanceRecordRepository.count() > 0) {
            return;
        }

        List<Equipment> equipmentList = equipmentRepository.findAll();
        if (equipmentList.isEmpty()) {
            return;
        }

        Equipment treadmill = equipmentList.get(0);
        Equipment elliptical = equipmentList.size() > 1 ? equipmentList.get(1) : treadmill;
        Equipment legPress = equipmentList.size() > 4 ? equipmentList.get(4) : treadmill;

        List<MaintenanceRecord> sampleRecords = List.of(
                // Historical Completed Record
                MaintenanceRecord.builder()
                        .equipment(treadmill)
                        .type(MaintenanceType.ROUTINE)
                        .scheduledDate(LocalDate.of(2026, 5, 10))
                        .completedDate(LocalDate.of(2026, 5, 10))
                        .technician("Apex Tech Services")
                        .cost(new BigDecimal("150.00"))
                        .notes("Belt tensioning and motor recalibration completed successfully.")
                        .status(MaintenanceStatus.COMPLETED)
                        .build(),

                // Currently In-Progress / Scheduled
                MaintenanceRecord.builder()
                        .equipment(elliptical)
                        .type(MaintenanceType.REPAIR)
                        .scheduledDate(LocalDate.now().plusDays(2))
                        .technician("Precor Official Vendor")
                        .cost(new BigDecimal("320.00"))
                        .notes("Replacing resistance flywheel sensor.")
                        .status(MaintenanceStatus.SCHEDULED)
                        .build(),

                // Overdue Task
                MaintenanceRecord.builder()
                        .equipment(legPress)
                        .type(MaintenanceType.INSPECTION)
                        .scheduledDate(LocalDate.now().minusDays(10))
                        .technician("Internal Maintenance - John Doe")
                        .notes("Safety latch inspection overdue.")
                        .status(MaintenanceStatus.OVERDUE)
                        .build()
        );

        maintenanceRecordRepository.saveAll(sampleRecords);
    }
}
