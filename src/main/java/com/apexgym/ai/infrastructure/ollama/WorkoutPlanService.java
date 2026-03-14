package com.apexgym.ai.infrastructure.ollama;

import com.apexgym.ai.domain.AiPromptProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class WorkoutPlanService {

    private final OllamaService ollamaService;
    private final AiPromptProvider aiPromptProvider;
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public List<String> getWeeklyWorkoutPlanParallel(int daysPerWeek, String goals, int exp, List<String> equip) {
        List<String> days = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        List<CompletableFuture<String>> futures = days.stream()
                .limit(daysPerWeek)
                .map(dayName -> CompletableFuture.supplyAsync(() ->
                        generateSingleDay(dayName, goals, exp, equip), executor))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private String generateSingleDay(String dayName, String goals, int exp, List<String> equip) {
        String prompt = aiPromptProvider.getWorkoutPlanPrompt(dayName, goals, exp, equip);
        String response = ollamaService.getJsonResponse(prompt);

        response = response.replaceAll("```json\\n?", "").replaceAll("```", "").trim();
        return response;
    }
}
