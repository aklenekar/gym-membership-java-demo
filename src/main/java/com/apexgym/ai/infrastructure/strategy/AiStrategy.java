package com.apexgym.ai.infrastructure.strategy;

import com.apexgym.ai.dto.ClassRecommendationDTO;
import com.apexgym.ai.dto.FitnessClass;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AiStrategy {
    String getName();

    List<FitnessClass> getRecommendations(List<String> history, String goals, String level, String availability);

    List<ClassRecommendationDTO> getRecommendedClasses(String goals, String level, List<String> history, String availability);

    Flux<ClassRecommendationDTO> getRecommendedClassesStream(String goals, String level, List<String> history, String availability);

    List<String> generateWorkoutPlan(String goals, int daysPerWeek, int experienceYears, List<String> availableEquipment);

    String getNutritionPlan(String goals, double weight, int age, String level, List<String> dietaryRestrictions);

    Flux<String> chatResponse(String systemPrompt, String userMessage);
}
