package com.apexgym.booking.web;

import com.apexgym.ai.dto.FitnessClass;
import com.apexgym.ai.dto.ClassRecommendationDTO;
import com.apexgym.ai.service.AiService;
import com.apexgym.booking.dto.GymClassDTO;
import com.apexgym.booking.service.GymClassService;
import com.apexgym.shared.CommonHelper;
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
