package com.apexgym.external;

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
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public List<String> getWeeklyWorkoutPlanParallel(int daysPerWeek, String goals, int exp, List<String> equip) {
        List<String> days = List.of("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        // Create a list of tasks (one for each day)
        List<CompletableFuture<String>> futures = days.stream()
                .limit(daysPerWeek)
                .map(dayName -> CompletableFuture.supplyAsync(() ->
                        generateSingleDay(dayName, goals, exp, equip), executor))
                .toList();

        // Wait for all days to finish and join them into a list
        return futures.stream()
                .map(CompletableFuture::join)
                .toList();
    }

    private String generateSingleDay(String dayName, String goals, int exp, List<String> equip) {
        String prompt = String.format("""
                Generate a workout for %s ONLY.
                Goal: %s | Experience: %d yrs | Equipments: %s
                
                Format as JSON:
                {"day": "%s", "focus": "", "exercises": [{"name": "", "sets": 0, "reps": 0}], "duration": "", "rest": ""}
                
                Respond ONLY with raw JSON.
                """, dayName, goals, exp, equip, dayName);

        String response = ollamaService.getJsonResponse(prompt);

        response = response.replaceAll("```json\\n?", "").replaceAll("```", "").trim();
        return response;
    }
}
