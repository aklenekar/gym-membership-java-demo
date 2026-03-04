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
            
            Goal: %s
            Weight: %.1f kg
            Age: %d
            Activity Level: %s
            Dietary Restrictions: %s
            
            Provide:
            1. Daily calorie target
            2. Macro split (protein/carbs/fats)
            3. Meal timing recommendations
            4. 3 sample meals
            5. Supplement suggestions
            
            Format as JSON.
            """, goal, weight, age, activityLevel, dietaryRestrictions);

        return ollamaService.getAiResponse(prompt);
    }
}
