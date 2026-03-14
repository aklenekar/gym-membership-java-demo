package com.apexgym.controller;

import com.apexgym.dto.ai.*;
import com.apexgym.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final CommonHelper commonHelper;

    @PostMapping("/recommend/classes")
    public ResponseEntity<List<ClassRecommendationDTO>> recommendClasses() {
        String email = commonHelper.getCurrentUserEmail();
        List<ClassRecommendationDTO> recommendations = aiService.getRecommendedClasses(email);
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping(value = "/v2/recommend/classes", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ClassRecommendationDTO> recommendClassesStream() {
        String email = commonHelper.getCurrentUserEmail();
        return aiService.getRecommendedClassesStreamResponse(email);
    }

    @PostMapping("/workout/plan")
    public ResponseEntity<List<String>> generateWorkoutPlan() {
        String email = commonHelper.getCurrentUserEmail();
        List<String> plan = aiService.generateWorkoutPlan(email);
        return ResponseEntity.ok(plan);
    }

    @PostMapping("/nutrition/plan")
    public ResponseEntity<String> getNutritionPlan() {
        String email = commonHelper.getCurrentUserEmail();
        String plan = aiService.getNutritionPlan(email);
        return ResponseEntity.ok(plan);
    }

    @PostMapping(value = "/chat", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<String> chat(@RequestBody ChatRequest request) {
        return aiService.chatResponse(request);
    }
}
