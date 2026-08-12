package com.apexgym.admin.web;

import com.apexgym.staff.dto.TrainersResponseDTO;
import com.apexgym.admin.dto.*;
import com.apexgym.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // ============================================================
    // MEMBERS
    // GET /api/admin/members?search=john&plan=PRO&status=ACTIVE&page=0
    // ============================================================

    @GetMapping("/members")
    public ResponseEntity<AdminMembersResponseDTO> getMembers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String plan,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page
    ) {
        AdminMembersResponseDTO response = adminService.getMembers(search, plan, status, page);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // TRAINERS
    // GET /api/admin/trainers?search=sarah&specialty=Strength&status=ACTIVE
    // ============================================================

    @GetMapping("/trainers")
    public ResponseEntity<TrainersResponseDTO> getTrainers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String status
    ) {
        TrainersResponseDTO response = adminService.getAdminTrainers(search, specialty, status);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // CLASSES
    // GET /api/admin/classes?search=yoga&category=HIIT&day=TODAY
    // ============================================================

    @GetMapping("/classes")
    public ResponseEntity<AdminClassesResponseDTO> getClasses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String day
    ) {
        AdminClassesResponseDTO response = adminService.getClasses(search, category, day);
        return ResponseEntity.ok(response);
    }

    // ============================================================
    // REPORTS
    // GET /api/admin/reports?period=WEEK
    // period options: TODAY, WEEK, MONTH, QUARTER, YEAR
    // ============================================================

    @GetMapping("/reports")
    public ResponseEntity<AdminReportsResponseDTO> getReports(
            @RequestParam(defaultValue = "MONTH") String period
    ) {
        AdminReportsResponseDTO response = adminService.getReports(period);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponseDTO> getDashboard() {
        AdminDashboardResponseDTO response = adminService.getDashboard();
        return ResponseEntity.ok(response);
    }
}
