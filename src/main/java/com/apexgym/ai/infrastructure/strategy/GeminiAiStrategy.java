package com.apexgym.ai.infrastructure.strategy;

import com.apexgym.ai.domain.AiPromptProvider;
import com.apexgym.ai.dto.ChatMessageDTO;
import com.apexgym.ai.dto.ClassRecommendationDTO;
import com.apexgym.ai.dto.FitnessClass;
import com.apexgym.ai.dto.openrouter.OpenRouterResponse;
import com.apexgym.ai.infrastructure.gemini.GeminiJsonUtil;
import com.apexgym.ai.infrastructure.gemini.GeminiService;
import com.apexgym.ai.service.RecommendationParser;
import com.apexgym.booking.persistence.GymClass;
import com.apexgym.booking.service.GymClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class GeminiAiStrategy implements AiStrategy {

    private final GeminiService geminiService;
    private final AiPromptProvider aiPromptProvider;
    private final GymClassService gymClassService;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public String getName() {
        return "Gemini";
    }

    @Override
    public List<FitnessClass> getRecommendations(List<String> history, String goals, String level, String availability) {
        String prompt = aiPromptProvider.getGeneralRecommendationPrompt(history, goals, level, availability);
        try {
            return RecommendationParser.convertToJson(geminiService.getJsonResponse(prompt));
        } catch (Exception e) {
            throw new RuntimeException("Failed to get recommendations from Gemini", e);
        }
    }

    @Override
    public List<ClassRecommendationDTO> getRecommendedClasses(String goals, String level, List<String> history, String availability) {
        List<GymClass> gymClasses = gymClassService.findUpcomingClasses(LocalDateTime.now());
        String systemPrompt = aiPromptProvider.getClassRecommendationSystemPrompt(gymClasses);
        String userPrompt = aiPromptProvider.getClassRecommendationUserPrompt(goals, level, history, availability);
        String response = geminiService.getAiResponse(systemPrompt, userPrompt);
        return GeminiJsonUtil.parseClassRecommendations(response);
    }

    @Override
    public Flux<ClassRecommendationDTO> getRecommendedClassesStream(String goals, String level, List<String> history, String availability) {
        List<GymClass> gymClasses = gymClassService.findUpcomingClasses(LocalDateTime.now());
        String systemPrompt = aiPromptProvider.getClassRecommendationSystemPrompt(gymClasses);
        String userPrompt = aiPromptProvider.getClassRecommendationUserPrompt(goals, level, history, availability);
        return geminiService.getRecommendationsStream(systemPrompt, userPrompt);
    }

    @Override
    public List<String> generateWorkoutPlan(String goals, int daysPerWeek, int experienceYears, List<String> availableEquipment) {
        List<String> days = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        List<CompletableFuture<String>> futures = days.stream()
                .limit(daysPerWeek)
                .map(dayName -> CompletableFuture.supplyAsync(() -> {
                    String prompt = aiPromptProvider.getWorkoutPlanPrompt(dayName, goals, experienceYears, availableEquipment);
                    return GeminiJsonUtil.clean(geminiService.getJsonResponse(prompt));
                }, executor))
                .toList();

        return futures.stream().map(CompletableFuture::join).toList();
    }

    @Override
    public String getNutritionPlan(String goals, double weight, int age, String level, List<String> dietaryRestrictions) {
        String prompt = aiPromptProvider.getNutritionPlanPrompt(goals, weight, age, level, dietaryRestrictions);
        return GeminiJsonUtil.clean(geminiService.getJsonResponse(prompt));
    }

    @Override
    public Flux<String> chatResponse(String systemPrompt, String userMessage) {
        return geminiService.streamAiResponse(systemPrompt, userMessage);
    }

    @Override
    public Flux<OpenRouterResponse> chatResponseWithHistory(String systemPrompt, List<ChatMessageDTO> conversationHistory, String userMessage, List<Map<String, Object>> tools) {
        return null;
    }
}