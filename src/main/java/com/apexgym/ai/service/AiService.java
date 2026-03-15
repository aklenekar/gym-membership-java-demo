package com.apexgym.ai.service;

import com.apexgym.ai.dto.ChatRequest;
import com.apexgym.ai.dto.ClassRecommendationDTO;
import com.apexgym.ai.dto.FitnessClass;
import com.apexgym.booking.dto.ClassAttendance;
import com.apexgym.booking.service.GymClassService;
import com.apexgym.dto.UserProfile;
import com.apexgym.ai.infrastructure.strategy.AiStrategy;
import com.apexgym.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiService {

    private final AiStrategy aiStrategy;
    private final ProfileService profileService;
    private final GymClassService gymClassService;

    public List<FitnessClass> getRecommendations(String email) throws Exception {
        UserProfile userProfile = profileService.getCurrentUser(email);
        List<ClassAttendance> history = gymClassService.getAttendanceHistory(email);

        return aiStrategy.getRecommendations(
                history.stream().map(ClassAttendance::className).collect(Collectors.toList()),
                userProfile.goals(),
                userProfile.level(),
                userProfile.availability()
        );
    }

    public List<ClassRecommendationDTO> getRecommendedClasses(String email) {
        UserProfile userProfile = profileService.getCurrentUser(email);
        List<ClassAttendance> history = gymClassService.getAttendanceHistory(email);

        return aiStrategy.getRecommendedClasses(
                userProfile.goals(),
                userProfile.level(),
                history.stream().map(ClassAttendance::className).collect(Collectors.toList()),
                userProfile.availability()
        );
    }

    public Flux<ClassRecommendationDTO> getRecommendedClassesStreamResponse(String email) {
        UserProfile userProfile = profileService.getCurrentUser(email);
        List<ClassAttendance> history = gymClassService.getAttendanceHistory(email);

        return aiStrategy.getRecommendedClassesStream(
                userProfile.goals(),
                userProfile.level(),
                history.stream().map(ClassAttendance::className).collect(Collectors.toList()),
                userProfile.availability()
        );
    }

    public List<String> generateWorkoutPlan(String email) {
        UserProfile userProfile = profileService.getCurrentUser(email);
        List<String> availableEquipment = List.of("Drill", "Saw", "Hammer", "Level");
        return aiStrategy.generateWorkoutPlan(userProfile.goals(), 3, 5, availableEquipment);
    }

    public String getNutritionPlan(String email) {
        UserProfile userProfile = profileService.getCurrentUser(email);
        return aiStrategy.getNutritionPlan(userProfile.goals(), 80, 32, userProfile.level(), Collections.emptyList());
    }

    public Flux<String> chatResponse(ChatRequest request) {
        String systemPrompt = "You are a helpful fitness assistant for ApexGym.";
        return aiStrategy.chatResponse(systemPrompt, request.message());
    }
}
