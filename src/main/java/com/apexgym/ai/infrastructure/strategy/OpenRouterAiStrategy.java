package com.apexgym.ai.infrastructure.strategy;

import com.apexgym.ai.domain.AiPromptProvider;
import com.apexgym.ai.dto.ClassRecommendationDTO;
import com.apexgym.ai.dto.FitnessClass;
import com.apexgym.ai.infrastructure.openrouter.OpenRouterService;
import com.apexgym.ai.service.RecommendationParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Primary
@Component
@RequiredArgsConstructor
@Slf4j
public class OpenRouterAiStrategy implements AiStrategy {

    private final OpenRouterService openRouterService;
    private final AiPromptProvider aiPromptProvider;
    private final ObjectMapper objectMapper;

    @Override
    public String getName() {
        return "OpenRouter";
    }

    @Override
    public List<FitnessClass> getRecommendations(List<String> history, String goals, String level, String availability) {
        String prompt = aiPromptProvider.getGeneralRecommendationPrompt(history, goals, level, availability);
        try {
            return RecommendationParser.convertToJson(openRouterService.getJsonResponse(prompt));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get recommendations from OpenRouter", e);
        }
    }

    @Override
    public List<ClassRecommendationDTO> getRecommendedClasses(String goals, String level, List<String> history, String availability) {
        String systemPrompt = aiPromptProvider.getClassRecommendationSystemPrompt();
        String userPrompt = aiPromptProvider.getClassRecommendationUserPrompt(goals, level, history, availability);
        String response = openRouterService.getAiResponse(systemPrompt, userPrompt);
        response = response.replaceAll("```json\\n?", "").replaceAll("```", "").trim();

        try {
            return objectMapper.readValue(response, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("Failed to parse OpenRouter response: {}", response, e);
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    @Override
    public Flux<ClassRecommendationDTO> getRecommendedClassesStream(String goals, String level, List<String> history, String availability) {
        // OpenRouter free model streaming returns raw text chunks; aggregate then emit single result
        List<ClassRecommendationDTO> recs = getRecommendedClasses(goals, level, history, availability);
        return Flux.fromIterable(recs);
    }

    @Override
    public List<String> generateWorkoutPlan(String goals, int daysPerWeek, int experienceYears, List<String> availableEquipment) {
        List<String> days = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        var executor = Executors.newVirtualThreadPerTaskExecutor();

        List<CompletableFuture<String>> futures = days.stream()
                .limit(daysPerWeek)
                .map(dayName -> CompletableFuture.supplyAsync(() -> {
                    String prompt = aiPromptProvider.getWorkoutPlanPrompt(dayName, goals, experienceYears, availableEquipment);
                    String response = openRouterService.getJsonResponse(prompt);
                    return response.replaceAll("```json\\n?", "").replaceAll("```", "").trim();
                }, executor))
                .toList();

        return futures.stream().map(CompletableFuture::join).toList();
    }

    @Override
    public String getNutritionPlan(String goals, double weight, int age, String level, List<String> dietaryRestrictions) {
        String prompt = aiPromptProvider.getNutritionPlanPrompt(goals, weight, age, level, dietaryRestrictions);
        String response = openRouterService.getJsonResponse(prompt);
        return response.replaceAll("```json\\n?", "").replaceAll("```", "").trim();
    }

    @Override
    public Flux<String> chatResponse(String systemPrompt, String userMessage) {
        return openRouterService.streamAiResponse(systemPrompt, userMessage);
    }
}