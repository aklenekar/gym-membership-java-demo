package com.apexgym.external;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkoutPlanService {

    private final OllamaService ollamaService;

    public String generateWeeklyPlan(
            String goals,
            int daysPerWeek,
            int experienceYears,
            List<String> availableEquipment
    ) {
        String prompt = String.format("""
                Create a %d-day weekly workout plan for:
                
                Goals: %s
                Experience: %d years
                Equipment: %s
                
                For each day, provide:
                - Day name
                - Focus (e.g., Upper Body, Cardio)
                - 5-8 exercises with sets/reps
                - Estimated duration
                - Rest periods
                
                Respond ONLY with a JSON array, no markdown, no extra text, no comments.
                """, daysPerWeek, goals, experienceYears, availableEquipment);

        String response = ollamaService.getAiResponse(prompt);

        response = response.replaceAll("```json\\n?", "").replaceAll("```", "").trim();

        return response;
    }
}
