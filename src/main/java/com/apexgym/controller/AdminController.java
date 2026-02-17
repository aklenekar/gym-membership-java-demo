package com.apexgym.controller;

import com.apexgym.dto.TrainersResponseDTO;
import com.apexgym.dto.admin.*;
import com.apexgym.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

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
    public ResponseEntity<?> getMembers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String plan,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page
    ) {
        try {
            AdminMembersResponseDTO response = adminService.getMembers(search, plan, status, page);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return error("Failed to fetch members", e);
        }
    }

    // ============================================================
    // TRAINERS
    // GET /api/admin/trainers?search=sarah&specialty=Strength&status=ACTIVE
    // ============================================================

    @GetMapping("/trainers")
    public ResponseEntity<?> getTrainers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String status
    ) {
        try {
            TrainersResponseDTO response = adminService.getAdminTrainers(search, specialty, status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return error("Failed to fetch trainers", e);
        }
    }

    // ============================================================
    // CLASSES
    // GET /api/admin/classes?search=yoga&category=HIIT&day=TODAY
    // ============================================================

    @GetMapping("/classes")
    public ResponseEntity<?> getClasses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String day
    ) {
        try {
            AdminClassesResponseDTO response = adminService.getClasses(search, category, day);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return error("Failed to fetch classes", e);
        }
    }

    // ============================================================
    // REPORTS
    // GET /api/admin/reports?period=WEEK
    // period options: TODAY, WEEK, MONTH, QUARTER, YEAR
    // ============================================================

    @GetMapping("/reports")
    public ResponseEntity<?> getReports(
            @RequestParam(defaultValue = "MONTH") String period
    ) {
        try {
            AdminReportsResponseDTO response = adminService.getReports(period);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return error("Failed to fetch reports", e);
        }
    }

    private ResponseEntity<?> error(String message, Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("message", message);
        error.put("error", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}