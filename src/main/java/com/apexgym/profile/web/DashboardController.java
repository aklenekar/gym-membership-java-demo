package com.apexgym.profile.web;

import com.apexgym.profile.dto.DashboardResponseDTO;
import com.apexgym.profile.service.DashboardService;
import com.apexgym.shared.CommonHelper;
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
