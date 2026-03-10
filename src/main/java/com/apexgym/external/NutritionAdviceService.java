package com.apexgym.external;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NutritionAdviceService {

    private final OllamaService ollamaService;

    public String getNutritionPlan(
            String goal,
            double weight,
            int age,
            String activityLevel,
            List<String> dietaryRestrictions
    ) {
        String prompt = String.format("""
                Create a nutrition plan for:
                Goal: %s | Weight: %.1f kg | Age: %d | Activity: %s | Restrictions: %s
                
                Response must follow this exact JSON structure:
                {"dailyCalorieTarget":0,"macroSplit":{"protein":0,"carbs":0,"fats":0},"mealTimingRecommendations":{},"sampleMeals":[{"meal":"","food":""}],"supplementSuggestions":[]}
                
                Respond ONLY with raw JSON.
                """, goal, weight, age, activityLevel, dietaryRestrictions);

        String response = ollamaService.getJsonResponse(prompt);

        response = response.replaceAll("```json\\n?", "").replaceAll("```", "").trim();
        return response;
    }
}
