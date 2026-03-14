package com.apexgym.ai.infrastructure.ollama;

import com.apexgym.ai.domain.AiPromptProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NutritionAdviceService {

    private final OllamaService ollamaService;
    private final AiPromptProvider aiPromptProvider;

    public String getNutritionPlan(
            String goal,
            double weight,
            int age,
            String activityLevel,
            List<String> dietaryRestrictions
    ) {
        String prompt = aiPromptProvider.getNutritionPlanPrompt(goal, weight, age, activityLevel, dietaryRestrictions);
        String response = ollamaService.getJsonResponse(prompt);

        response = response.replaceAll("```json\\n?", "").replaceAll("```", "").trim();
        return response;
    }
}
