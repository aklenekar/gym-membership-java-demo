package com.apexgym.controller;

import com.apexgym.dto.FitnessClass;
import com.apexgym.dto.GymClassDTO;
import com.apexgym.external.AiService;
import com.apexgym.service.GymClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/classes")
@RequiredArgsConstructor
public class GymClassController {

    private final GymClassService service;
    private final CommonHelper commonHelper;
    private final AiService aiService;

    /**
     * -------------------------------------------------------------
     * GET  /api/classes
     * Optional query params: category, instructor, day
     * -------------------------------------------------------------
     */
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String instructor,
            @RequestParam(required = false) String day) {
        try {
            String email = commonHelper.getCurrentUserEmail();
            List<GymClassDTO> classes = service.findByFilters(category, instructor, day, email);
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to load classes");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendations() {
        try {
            String email = commonHelper.getCurrentUserEmail();
            List<FitnessClass> response = aiService.getRecommendations(email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Failed to load classes");
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

}
