package com.apexgym.equipment.web;

import com.apexgym.equipment.dto.*;
import com.apexgym.equipment.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/admin/equipment/maintenance")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @GetMapping
    public ResponseEntity<List<MaintenanceRecordDTO>> getMaintenanceRecords(
            @RequestParam(required = false) Long equipmentId) {
        if (equipmentId != null) {
            return ResponseEntity.ok(maintenanceService.getMaintenanceByEquipmentId(equipmentId));
        }
        return ResponseEntity.ok(maintenanceService.getAllMaintenanceRecords());
    }

    @PostMapping("/schedule")
    public ResponseEntity<MaintenanceRecordDTO> scheduleMaintenance(
            @Valid @RequestBody ScheduleMaintenanceRequest request) {
        MaintenanceRecordDTO created = maintenanceService.scheduleMaintenance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<MaintenanceRecordDTO> completeMaintenance(
            @PathVariable Long id,
            @Valid @RequestBody CompleteMaintenanceRequest request) {
        return ResponseEntity.ok(maintenanceService.completeMaintenance(id, request));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<MaintenanceRecordDTO> cancelMaintenance(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceService.cancelMaintenance(id));
    }

    @GetMapping("/calendar")
    public ResponseEntity<MaintenanceCalendarResponseDTO> getCalendarView(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(maintenanceService.getMaintenanceCalendar(startDate, endDate));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<MaintenanceRecordDTO>> getOverdueMaintenance() {
        return ResponseEntity.ok(maintenanceService.getOverdueMaintenance());
    }
}
