package com.apexgym.ai.infrastructure.strategy;

import com.apexgym.dto.FitnessClass;
import com.apexgym.dto.ai.ClassRecommendationDTO;
import com.apexgym.ai.domain.AiPromptProvider;
import com.apexgym.ai.infrastructure.ollama.*;
import com.apexgym.service.RecommendationParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OllamaAiStrategy implements AiStrategy {

    private final OllamaService ollamaService;
    private final ClassRecommendationService classRecommendationService;
    private final WorkoutPlanService workoutPlanService;
    private final NutritionAdviceService nutritionService;
    private final AiPromptProvider aiPromptProvider;

    @Override
    public String getName() {
        return "Ollama";
    }

    @Override
    public List<FitnessClass> getRecommendations(List<String> history, String goals, String level, String availability) {
        String prompt = aiPromptProvider.getGeneralRecommendationPrompt(history, goals, level, availability);
        try {
            return RecommendationParser.convertToJson(ollamaService.getJsonResponse(prompt));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get recommendations from Ollama", e);
        }
    }

    @Override
    public List<ClassRecommendationDTO> getRecommendedClasses(String goals, String level, List<String> history, String availability) {
        return classRecommendationService.getRecommendations(goals, level, history, availability);
    }

    @Override
    public Flux<ClassRecommendationDTO> getRecommendedClassesStream(String goals, String level, List<String> history, String availability) {
        return classRecommendationService.getRecommendationsStream(goals, level, history, availability);
    }

    @Override
    public List<String> generateWorkoutPlan(String goals, int daysPerWeek, int experienceYears, List<String> availableEquipment) {
        return workoutPlanService.getWeeklyWorkoutPlanParallel(daysPerWeek, goals, experienceYears, availableEquipment);
    }

    @Override
    public String getNutritionPlan(String goals, double weight, int age, String level, List<String> dietaryRestrictions) {
        return nutritionService.getNutritionPlan(goals, weight, age, level, dietaryRestrictions);
    }

    @Override
    public Flux<String> chatResponse(String systemPrompt, String userMessage) {
        return ollamaService.streamAiResponse(systemPrompt, userMessage);
    }
}
