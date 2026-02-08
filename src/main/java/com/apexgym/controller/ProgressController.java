package com.apexgym.controller;

import com.apexgym.dto.ProgressResponseDTO;
import com.apexgym.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final CommonHelper commonHelper;
    private final ProgressService progressService;

    @GetMapping
    public ResponseEntity<?> getProgress() {
        try {
            String email = commonHelper.getCurrentUserEmail();
            ProgressResponseDTO progress = progressService.getProgress(email);
            return ResponseEntity.ok(progress);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to fetch progress");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}