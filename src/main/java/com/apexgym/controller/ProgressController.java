package com.apexgym.controller;

import com.apexgym.dto.ProgressResponseDTO;
import com.apexgym.service.ProgressService;
import com.apexgym.shared.CommonHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final CommonHelper commonHelper;
    private final ProgressService progressService;

    @GetMapping
    public ResponseEntity<ProgressResponseDTO> getProgress() {
        String email = commonHelper.getCurrentUserEmail();
        ProgressResponseDTO progress = progressService.getProgress(email);
        return ResponseEntity.ok(progress);
    }
}
