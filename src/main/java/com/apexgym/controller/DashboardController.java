package com.apexgym.controller;

import com.apexgym.dto.DashboardResponseDTO;
import com.apexgym.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final CommonHelper commonHelper;
    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> getDashboard() {
        String email = commonHelper.getCurrentUserEmail();
        DashboardResponseDTO dashboard = dashboardService.getDashboardData(email);
        return ResponseEntity.ok(dashboard);
    }
}
