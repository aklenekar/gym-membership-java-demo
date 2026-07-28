package com.apexgym.equipment.service;

import com.apexgym.equipment.dto.*;
import com.apexgym.equipment.entity.Equipment;
import com.apexgym.equipment.entity.enums.EquipmentCategory;
import com.apexgym.equipment.entity.enums.EquipmentStatus;
import com.apexgym.equipment.entity.enums.MaintenanceStatus;
import com.apexgym.equipment.mapper.EquipmentMapper;
import com.apexgym.equipment.persistence.EquipmentRepository;
import com.apexgym.equipment.persistence.MaintenanceRecordRepository;
import com.apexgym.equipment.persistence.specification.EquipmentSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final EquipmentMapper equipmentMapper;

    @Transactional(readOnly = true)
    public EquipmentInventoryResponseDTO getEquipmentInventory(
            EquipmentCategory category,
            EquipmentStatus status,
            String location,
            String search,
            Pageable pageable) {

        Specification<Equipment> spec = EquipmentSpecifications.withFilters(category, status, location, search);
        Page<EquipmentDTO> equipmentPage = equipmentRepository.findAll(spec, pageable)
                .map(equipmentMapper::toDto);

        long totalEquipment = equipmentRepository.count();
        long operational = countByStatus(EquipmentStatus.OPERATIONAL);
        long underMaintenance = countByStatus(EquipmentStatus.UNDER_MAINTENANCE);
        long outOfService = countByStatus(EquipmentStatus.OUT_OF_SERVICE);

        return new EquipmentInventoryResponseDTO(
                equipmentPage,
                totalEquipment,
                operational,
                underMaintenance,
                outOfService
        );
    }

    @Transactional(readOnly = true)
    public EquipmentDTO getEquipmentById(Long id) {
        Equipment equipment = findByIdOrThrow(id);
        return equipmentMapper.toDto(equipment);
    }

    @Transactional
    public EquipmentDTO createEquipment(CreateEquipmentRequest request) {
        Equipment equipment = equipmentMapper.toEntity(request);
        Equipment saved = equipmentRepository.save(equipment);
        return equipmentMapper.toDto(saved);
    }

    @Transactional
    public EquipmentDTO updateEquipment(Long id, UpdateEquipmentRequest request) {
        Equipment equipment = findByIdOrThrow(id);
        equipmentMapper.updateEntityFromRequest(request, equipment);
        Equipment updated = equipmentRepository.save(equipment);
        return equipmentMapper.toDto(updated);
    }

    @Transactional
    public EquipmentDTO updateEquipmentStatus(Long id, EquipmentStatus newStatus) {
        Equipment equipment = findByIdOrThrow(id);
        equipment.setStatus(newStatus);
        Equipment updated = equipmentRepository.save(equipment);
        return equipmentMapper.toDto(updated);
    }

    @Transactional
    public void deleteEquipment(Long id) {
        if (!equipmentRepository.existsById(id)) {
            throw new RuntimeException("Equipment not found with id: " + id);
        }
        equipmentRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public EquipmentStatsDTO getEquipmentStats() {
        long totalEquipment = equipmentRepository.count();

        BigDecimal totalAssetValue = equipmentRepository.findAll().stream()
                .map(Equipment::getPurchasePrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long upcomingCount = maintenanceRecordRepository.countByStatus(MaintenanceStatus.SCHEDULED);
        long overdueCount = maintenanceRecordRepository.findOverdue(LocalDate.now()).size();

        return new EquipmentStatsDTO(
                totalEquipment,
                totalAssetValue,
                upcomingCount,
                overdueCount
        );
    }

    private Equipment findByIdOrThrow(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + id));
    }

    private long countByStatus(EquipmentStatus status) {
        return equipmentRepository.findAll(EquipmentSpecifications.withFilters(null, status, null, null)).size();
    }
}