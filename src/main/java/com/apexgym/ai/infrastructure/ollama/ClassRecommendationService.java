package com.apexgym.ai.infrastructure.ollama;

import com.apexgym.ai.dto.ClassRecommendationDTO;
import com.apexgym.ai.domain.AiPromptProvider;
import com.apexgym.booking.persistence.GymClass;
import com.apexgym.booking.service.GymClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassRecommendationService {

    private final ObjectMapper objectMapper;
    private final OllamaService ollamaService;
    private final AiPromptProvider aiPromptProvider;
    private final GymClassService gymClassService;

    public List<ClassRecommendationDTO> getRecommendations(String userGoals, String fitnessLevel, List<String> pastClasses, String availability) {
        List<GymClass> gymClasses = gymClassService.findUpcomingClasses(LocalDateTime.now());
        String systemPrompt = aiPromptProvider.getClassRecommendationSystemPrompt(gymClasses);
        String userPrompt = aiPromptProvider.getClassRecommendationUserPrompt(userGoals, fitnessLevel, pastClasses, availability);
        String response = ollamaService.getAiResponse(systemPrompt, userPrompt);

        response = response.replaceAll("```json\\n?", "").replaceAll("```", "").trim();
        try {
            return objectMapper.readValue(response, new TypeReference<List<ClassRecommendationDTO>>() {});
        } catch (Exception e) {
            log.error("Failed to parse Ollama response: {}", response, e);
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    public Flux<ClassRecommendationDTO> getRecommendationsStream(String userGoals, String fitnessLevel, List<String> pastClasses, String availability) {
        List<GymClass> gymClasses = gymClassService.findUpcomingClasses(LocalDateTime.now());
        String systemPrompt = aiPromptProvider.getClassRecommendationSystemPrompt(gymClasses);
        String userPrompt = aiPromptProvider.getClassRecommendationUserPrompt(userGoals, fitnessLevel, pastClasses, availability);
        return ollamaService.getRecommendationsStream(systemPrompt, userPrompt);
    }
}
