package com.apexgym.controller;

import com.apexgym.dto.FitnessClass;
import com.apexgym.dto.GymClassDTO;
import com.apexgym.dto.ai.ClassRecommendationDTO;
import com.apexgym.external.AiService;
import com.apexgym.service.GymClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<GymClassDTO>> getAll(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String instructor,
            @RequestParam(required = false) String day) {
        String email = commonHelper.getCurrentUserEmail();
        List<GymClassDTO> classes = service.findByFilters(category, instructor, day, email);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<FitnessClass>> getRecommendations() throws Exception {
        String email = commonHelper.getCurrentUserEmail();
        List<FitnessClass> response = aiService.getRecommendations(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/recommendations/v2")
    public ResponseEntity<List<ClassRecommendationDTO>> getRecommendationsV2() {
        String email = commonHelper.getCurrentUserEmail();
        List<ClassRecommendationDTO> response = aiService.getRecommendedClasses(email);
        return ResponseEntity.ok(response);
    }

}
