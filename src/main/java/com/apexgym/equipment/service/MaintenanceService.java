package com.apexgym.equipment.service;

import com.apexgym.equipment.dto.*;
import com.apexgym.equipment.entity.Equipment;
import com.apexgym.equipment.entity.MaintenanceRecord;
import com.apexgym.equipment.entity.enums.EquipmentStatus;
import com.apexgym.equipment.entity.enums.MaintenanceStatus;
import com.apexgym.equipment.mapper.MaintenanceMapper;
import com.apexgym.equipment.persistence.EquipmentRepository;
import com.apexgym.equipment.persistence.MaintenanceRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final EquipmentRepository equipmentRepository;
    private final MaintenanceMapper maintenanceMapper;

    @Transactional(readOnly = true)
    public List<MaintenanceRecordDTO> getAllMaintenanceRecords() {
        return maintenanceRecordRepository.findAll().stream()
                .map(maintenanceMapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MaintenanceRecordDTO> getMaintenanceByEquipmentId(Long equipmentId) {
        return maintenanceRecordRepository.findByEquipmentId(equipmentId).stream()
                .map(maintenanceMapper::toDto)
                .toList();
    }

    @Transactional
    public MaintenanceRecordDTO scheduleMaintenance(ScheduleMaintenanceRequest request) {
        Equipment equipment = equipmentRepository.findById(request.equipmentId())
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + request.equipmentId()));

        MaintenanceRecord record = maintenanceMapper.toEntity(request);
        record.setEquipment(equipment);
        record.setStatus(MaintenanceStatus.SCHEDULED);

        // Update equipment next maintenance date
        equipment.setNextMaintenanceDate(request.scheduledDate());
        equipmentRepository.save(equipment);

        MaintenanceRecord saved = maintenanceRecordRepository.save(record);
        return maintenanceMapper.toDto(saved);
    }

    @Transactional
    public MaintenanceRecordDTO completeMaintenance(Long id, CompleteMaintenanceRequest request) {
        MaintenanceRecord record = findByIdOrThrow(id);

        record.setCompletedDate(request.completedDate());
        if (request.cost() != null) record.setCost(request.cost());
        if (request.notes() != null) record.setNotes(request.notes());
        record.setStatus(MaintenanceStatus.COMPLETED);

        // Synchronize equipment state
        Equipment equipment = record.getEquipment();
        equipment.setLastMaintenanceDate(request.completedDate());
        if (equipment.getStatus() == EquipmentStatus.UNDER_MAINTENANCE) {
            equipment.setStatus(EquipmentStatus.OPERATIONAL);
        }
        equipmentRepository.save(equipment);

        MaintenanceRecord updated = maintenanceRecordRepository.save(record);
        return maintenanceMapper.toDto(updated);
    }

    @Transactional
    public MaintenanceRecordDTO cancelMaintenance(Long id) {
        MaintenanceRecord record = findByIdOrThrow(id);
        record.setStatus(MaintenanceStatus.CANCELLED);
        MaintenanceRecord updated = maintenanceRecordRepository.save(record);
        return maintenanceMapper.toDto(updated);
    }

    @Transactional(readOnly = true)
    public MaintenanceCalendarResponseDTO getMaintenanceCalendar(LocalDate startDate, LocalDate endDate) {
        List<MaintenanceRecordDTO> scheduled = maintenanceRecordRepository.findAll().stream()
                .filter(m -> m.getScheduledDate() != null &&
                        !m.getScheduledDate().isBefore(startDate) &&
                        !m.getScheduledDate().isAfter(endDate))
                .map(maintenanceMapper::toDto)
                .toList();

        List<MaintenanceRecordDTO> overdue = getOverdueMaintenance();

        return new MaintenanceCalendarResponseDTO(scheduled, overdue);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceRecordDTO> getOverdueMaintenance() {
        return maintenanceRecordRepository.findOverdue(LocalDate.now()).stream()
                .map(maintenanceMapper::toDto)
                .toList();
    }

    private MaintenanceRecord findByIdOrThrow(Long id) {
        return maintenanceRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Maintenance record not found with id: " + id));
    }
}