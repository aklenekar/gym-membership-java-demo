package com.apexgym.shared.initializers.seeders;

import com.apexgym.equipment.entity.Equipment;
import com.apexgym.equipment.entity.enums.EquipmentCategory;
import com.apexgym.equipment.entity.enums.EquipmentStatus;
import com.apexgym.equipment.persistence.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EquipmentSeeder {

    private final EquipmentRepository equipmentRepository;

    public void seed() {
        if (equipmentRepository.count() > 0) {
            return; // Seed only if table is empty
        }

        List<Equipment> sampleEquipment = List.of(
                // Cardio Zone
                Equipment.builder()
                        .name("Pro Treadmill T800")
                        .category(EquipmentCategory.CARDIO)
                        .brand("LifeFitness")
                        .model("T800-X")
                        .serialNumber("LF-TR-2023-001")
                        .purchaseDate(LocalDate.of(2023, 1, 15))
                        .purchasePrice(new BigDecimal("3500.00"))
                        .location("Cardio Zone A")
                        .status(EquipmentStatus.OPERATIONAL)
                        .lastMaintenanceDate(LocalDate.of(2026, 5, 10))
                        .nextMaintenanceDate(LocalDate.of(2026, 8, 10))
                        .build(),

                Equipment.builder()
                        .name("Commercial Elliptical Trainer E5")
                        .category(EquipmentCategory.CARDIO)
                        .brand("Precor")
                        .model("EFX 835")
                        .serialNumber("PR-EL-2022-045")
                        .purchaseDate(LocalDate.of(2022, 6, 20))
                        .purchasePrice(new BigDecimal("2800.00"))
                        .location("Cardio Zone B")
                        .status(EquipmentStatus.UNDER_MAINTENANCE)
                        .lastMaintenanceDate(LocalDate.of(2026, 2, 1))
                        .nextMaintenanceDate(LocalDate.of(2026, 7, 15))
                        .build(),

                Equipment.builder()
                        .name("Air Bike Pro")
                        .category(EquipmentCategory.CARDIO)
                        .brand("Rogue Fitness")
                        .model("Echo Bike V2")
                        .serialNumber("RG-BK-2024-012")
                        .purchaseDate(LocalDate.of(2024, 3, 10))
                        .purchasePrice(new BigDecimal("900.00"))
                        .location("HIIT Area")
                        .status(EquipmentStatus.OPERATIONAL)
                        .lastMaintenanceDate(LocalDate.of(2026, 6, 1))
                        .nextMaintenanceDate(LocalDate.of(2026, 9, 1))
                        .build(),

                // Strength Zone
                Equipment.builder()
                        .name("Olympic Squat Rack Heavy Duty")
                        .category(EquipmentCategory.STRENGTH)
                        .brand("Hammer Strength")
                        .model("HD Elite")
                        .serialNumber("HS-SQ-2021-102")
                        .purchaseDate(LocalDate.of(2021, 11, 5))
                        .purchasePrice(new BigDecimal("2100.00"))
                        .location("Weight Room - Zone 1")
                        .status(EquipmentStatus.OPERATIONAL)
                        .lastMaintenanceDate(LocalDate.of(2026, 4, 12))
                        .nextMaintenanceDate(LocalDate.of(2026, 10, 12))
                        .build(),

                Equipment.builder()
                        .name("Leg Press Machine 45 Degree")
                        .category(EquipmentCategory.STRENGTH)
                        .brand("CybeX")
                        .model("Eagle NX")
                        .serialNumber("CX-LP-2020-088")
                        .purchaseDate(LocalDate.of(2020, 8, 18))
                        .purchasePrice(new BigDecimal("4200.00"))
                        .location("Weight Room - Zone 2")
                        .status(EquipmentStatus.OUT_OF_SERVICE)
                        .lastMaintenanceDate(LocalDate.of(2025, 12, 01))
                        .nextMaintenanceDate(LocalDate.of(2026, 6, 01))
                        .build(),

                // Free Weights
                Equipment.builder()
                        .name("Rubber Hex Dumbbell Set (5kg - 50kg)")
                        .category(EquipmentCategory.FREE_WEIGHTS)
                        .brand("Rogue Fitness")
                        .model("Hex DB-Set")
                        .serialNumber("RG-DB-2023-SET1")
                        .purchaseDate(LocalDate.of(2023, 2, 1))
                        .purchasePrice(new BigDecimal("3200.00"))
                        .location("Free Weights Area")
                        .status(EquipmentStatus.OPERATIONAL)
                        .lastMaintenanceDate(LocalDate.of(2026, 1, 10))
                        .nextMaintenanceDate(LocalDate.of(2026, 7, 10))
                        .build(),

                // Functional
                Equipment.builder()
                        .name("Functional Trainer Dual Pulley")
                        .category(EquipmentCategory.FUNCTIONAL)
                        .brand("Matrix")
                        .model("Aura Series")
                        .serialNumber("MX-FT-2022-301")
                        .purchaseDate(LocalDate.of(2022, 9, 30))
                        .purchasePrice(new BigDecimal("4800.00"))
                        .location("Functional Zone")
                        .status(EquipmentStatus.OPERATIONAL)
                        .lastMaintenanceDate(LocalDate.of(2026, 3, 15))
                        .nextMaintenanceDate(LocalDate.of(2026, 9, 15))
                        .build()
        );

        equipmentRepository.saveAll(sampleEquipment);
    }
}