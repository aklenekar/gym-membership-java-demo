package com.apexgym.equipment.entity;

import com.apexgym.equipment.entity.enums.EquipmentCategory;
import com.apexgym.equipment.entity.enums.EquipmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "equipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentCategory category;

    private String brand;
    private String model;

    @Column(unique = true)
    private String serialNumber;

    private LocalDate purchaseDate;
    private BigDecimal purchasePrice;
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentStatus status;

    private LocalDate lastMaintenanceDate;
    private LocalDate nextMaintenanceDate;
    private String imageUrl;
}
