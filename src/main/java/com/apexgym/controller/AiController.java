package com.apexgym.controller;

import com.apexgym.dto.ai.*;
import com.apexgym.external.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final CommonHelper commonHelper;

    @PostMapping("/recommend/classes")
    public ResponseEntity<List<ClassRecommendationDTO>> recommendClasses(
            /*@RequestBody ClassRecommendationRequest request*/
    ) {
        String email = commonHelper.getCurrentUserEmail();
        List<ClassRecommendationDTO> recommendations = aiService.getRecommendedClasses(email);
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/workout/plan")
    public ResponseEntity<String> generateWorkoutPlan(
            /*@RequestBody WorkoutPlanRequest request*/
    ) {
        String email = commonHelper.getCurrentUserEmail();
        String plan = aiService.generateWorkoutPlan(email);
        return ResponseEntity.ok(plan);
    }

    @PostMapping("/nutrition/plan")
    public ResponseEntity<String> getNutritionPlan(
            /*@RequestBody NutritionRequest request*/
    ) {
        String email = commonHelper.getCurrentUserEmail();
        String plan = aiService.getNutritionPlan(email);
        return ResponseEntity.ok(plan);
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody ChatRequest request) {
        // General fitness Q&A
        return ResponseEntity.ok("AI response");
    }
}
