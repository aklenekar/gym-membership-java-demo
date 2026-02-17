package com.apexgym.external;

import com.apexgym.dto.ClassAttendance;
import com.apexgym.dto.FitnessClass;
import com.apexgym.dto.UserProfile;
import com.apexgym.service.GymClassService;
import com.apexgym.service.ProfileService;
import com.apexgym.service.RecommendationParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AiService {

    private final OllamaService ollamaService;
    private final ProfileService profileService;
    private final GymClassService gymClassService;

    public List<FitnessClass> getRecommendations(String email) throws Exception {
        UserProfile userProfile = profileService.getCurrentUser(email);
        List<ClassAttendance> history = gymClassService.getAttendanceHistory(email);

        String prompt = """
        Based on this user's data:
        - Past classes: %s
        - Goals: %s
        - Fitness level: %s
        - Available times: %s
        
        Recommend 5 classes with reasoning.
        """.formatted(history, userProfile.getGoals(),
                userProfile.getLevel(), userProfile.getAvailability());

        return RecommendationParser.convertToJson(ollamaService.getAiResponse(prompt));
    }
}
