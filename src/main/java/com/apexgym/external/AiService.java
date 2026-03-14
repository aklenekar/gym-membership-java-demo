package com.apexgym.external;

import com.apexgym.dto.ClassAttendance;
import com.apexgym.dto.FitnessClass;
import com.apexgym.dto.UserProfile;
import com.apexgym.dto.ai.ChatRequest;
import com.apexgym.dto.ai.ClassRecommendationDTO;
import com.apexgym.service.GymClassService;
import com.apexgym.service.ProfileService;
import com.apexgym.service.RecommendationParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiService {

    private final OllamaService ollamaService;
    private final ProfileService profileService;
    private final GymClassService gymClassService;
    private final ClassRecommendationService classRecommendationService;
    private final WorkoutPlanService workoutPlanService;
    private final NutritionAdviceService nutritionService;

    public List<FitnessClass> getRecommendations(String email) throws Exception {
        UserProfile userProfile = profileService.getCurrentUser(email);
        List<ClassAttendance> history = gymClassService.getAttendanceHistory(email);

        String prompt = """
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
        """.formatted(history, userProfile.goals(),
                userProfile.level(), userProfile.availability());

        return RecommendationParser.convertToJson(ollamaService.getJsonResponse(prompt));
    }

    public List<ClassRecommendationDTO> getRecommendedClasses(String email) {
        UserProfile userProfile = profileService.getCurrentUser(email);
        List<ClassAttendance> history = gymClassService.getAttendanceHistory(email);

        return classRecommendationService.getRecommendations(userProfile.goals(), userProfile.level()
                , history.stream().map(ClassAttendance::className).collect(Collectors.toList()), userProfile.availability());
    }

    public Flux<ClassRecommendationDTO> getRecommendedClassesStreamResponse(String email) {
        UserProfile userProfile = profileService.getCurrentUser(email);
        List<ClassAttendance> history = gymClassService.getAttendanceHistory(email);

        return classRecommendationService.getRecommendationsStream(userProfile.goals(), userProfile.level()
                , history.stream().map(ClassAttendance::className).collect(Collectors.toList()), userProfile.availability());
    }

    public List<String> generateWorkoutPlan(String email) {
        UserProfile userProfile = profileService.getCurrentUser(email);
        List<String> availableEquipment = List.of("Drill", "Saw", "Hammer", "Level");
        return workoutPlanService.getWeeklyWorkoutPlanParallel(3, userProfile.goals(), 5, availableEquipment);
    }

    public String getNutritionPlan(String email) {
        UserProfile userProfile = profileService.getCurrentUser(email);
        return nutritionService.getNutritionPlan(userProfile.goals(), 80, 32, userProfile.level(), Collections.emptyList());
    }

    public Flux<String> chatResponse(ChatRequest request) {
        String systemPrompt = "You are a helpful fitness assistant for ApexGym.";
        return ollamaService.streamAiResponse(systemPrompt, request.message());
    }
}
