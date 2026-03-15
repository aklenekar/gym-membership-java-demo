package com.apexgym.ai.infrastructure.ollama;

import com.apexgym.ai.dto.ClassRecommendationDTO;
import com.apexgym.ai.domain.AiPromptProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassRecommendationService {

    private final ObjectMapper objectMapper;
    private final OllamaService ollamaService;
    private final AiPromptProvider aiPromptProvider;

    public List<ClassRecommendationDTO> getRecommendations(String userGoals, String fitnessLevel, List<String> pastClasses, String availability) {
        String systemPrompt = aiPromptProvider.getClassRecommendationSystemPrompt();
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
        String systemPrompt = aiPromptProvider.getClassRecommendationSystemPrompt();
        String userPrompt = aiPromptProvider.getClassRecommendationUserPrompt(userGoals, fitnessLevel, pastClasses, availability);
        return ollamaService.getRecommendationsStream(systemPrompt, userPrompt);
    }
}
