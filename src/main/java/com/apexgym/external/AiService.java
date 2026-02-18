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
        """.formatted(history, userProfile.getGoals(),
                userProfile.getLevel(), userProfile.getAvailability());

        return RecommendationParser.convertToJson(ollamaService.getAiResponse(prompt));
    }
}
