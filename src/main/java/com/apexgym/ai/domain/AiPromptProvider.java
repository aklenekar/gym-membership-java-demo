package com.apexgym.ai.domain;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class AiPromptProvider {

    public String getClassRecommendationSystemPrompt() {
        return """
            You are a professional fitness advisor at APEX GYM.
            Analyze the user profile and recommend 5 gym classes.
            
            Respond ONLY with a JSON array, no markdown, no extra text.
            Format:
            [
              {
                "className": "HIIT Bootcamp",
                "reasoning": "Matches your high-intensity goals",
                "benefits": ["Burns calories", "Builds endurance"],
                "matchPercentage": 95
              }
            ]
            """;
    }

    public String getClassRecommendationUserPrompt(String goals, String level, List<String> history, String availability) {
        return """
            User Profile:
            - Goals: %s
            - Fitness Level: %s
            - Past Classes: %s
            - Preferred Time: %s
            
            Available Classes at APEX GYM:
            1. HIIT Bootcamp - High intensity interval training (60min)
            2. Yoga Flow - Flexibility and mindfulness (75min)
            3. Strength Training - Muscle building and conditioning (90min)
            4. Cycling Endurance - Cardio and stamina (45min)
            5. Boxing Fundamentals - Combat sports and conditioning (60min)
            6. Pilates Core - Core strength and stability (60min)
            7. CrossFit - Functional fitness (75min)
            8. Zumba Dance - Cardio through dance (50min)
            
            Recommend the best 5 classes.
            """.formatted(goals, level, history, availability);
    }

    public String getWorkoutPlanPrompt(String dayName, String goals, int experienceYears, List<String> equipment) {
        return """
            Generate a workout for %s ONLY.
            Goal: %s | Experience: %d yrs | Equipments: %s
            
            Format as JSON:
            {"day": "%s", "focus": "", "exercises": [{"name": "", "sets": 0, "reps": 0}], "duration": "", "rest": ""}
            
            Respond ONLY with raw JSON.
            """.formatted(dayName, goals, experienceYears, equipment, dayName);
    }

    public String getNutritionPlanPrompt(String goals, double weight, int age, String activityLevel, List<String> restrictions) {
        return """
            Create a nutrition plan for:
            Goal: %s | Weight: %.1f kg | Age: %d | Activity: %s | Restrictions: %s
            
            Response must follow this exact JSON structure:
            {"dailyCalorieTarget":0,"macroSplit":{"protein":0,"carbs":0,"fats":0},"mealTimingRecommendations":{},"sampleMeals":[{"meal":"","food":""}],"supplementSuggestions":[]}
            
            Respond ONLY with raw JSON.
            """.formatted(goals, weight, age, activityLevel, restrictions);
    }

    public String getGeneralRecommendationPrompt(List<String> history, String goals, String level, String availability) {
        return """
            Act as a professional fitness coordinator. Based on the user data provided below, recommend 5 fitness classes.
            
            ### User Data:
            - Past classes: %s
            - Goals: %s
            - Fitness level: %s
            - Available times: %s
            
            ### Instructions:
            Return the recommendations strictly as a JSON object containing a list named "recommendations".\s
            Each object in the list must follow this schema:
            {
              "name": "Class Name",
              "reasoning": "A concise explanation of why this fits the user's goals, level, and schedule."
            }
            
            Do not include any introductory text, markdown formatting (like ```json), or follow-up remarks. Return only the raw JSON.
            """.formatted(history, goals, level, availability);
    }
}
