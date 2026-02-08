package com.apexgym.controller;

import com.apexgym.dto.DashboardResponseDTO;
import com.apexgym.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final CommonHelper commonHelper;
    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<?> getDashboard() {
        try {
            String email = commonHelper.getCurrentUserEmail();
            DashboardResponseDTO dashboard = dashboardService.getDashboardData(email);
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to load dashboard");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}