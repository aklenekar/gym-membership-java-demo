package com.apexgym.external;

import com.apexgym.dto.ai.ClassRecommendationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.lang.reflect.Type;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClassRecommendationService {

    private final ObjectMapper objectMapper;
    private final OllamaService ollamaService;

    String systemPrompt = """
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

    String userPrompt = """
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
            """;

    public List<ClassRecommendationDTO> getRecommendations(String userGoals, String fitnessLevel, List<String> pastClasses, String availability) {
        String userFormattedPrompt = String.format(userPrompt, userGoals, fitnessLevel, pastClasses, availability);
        String response = ollamaService.getAiResponse(systemPrompt, userFormattedPrompt);

        // Clean response (remove markdown if present)
        response = response.replaceAll("```json\\n?", "").replaceAll("```", "").trim();
        try {
            return objectMapper.readValue(response, new TypeReference<>() {
                @Override
                public Type getType() {
                    return super.getType();
                }
            });
        } catch (Exception e) {
            log.error("Failed to parse Ollama response: {}", response, e);
            throw new RuntimeException("Failed to parse AI response", e);
        }
    }

    public Flux<ClassRecommendationDTO> getRecommendationsStream(String userGoals, String fitnessLevel, List<String> pastClasses, String availability) {
        String userFormattedPrompt = String.format(userPrompt, userGoals, fitnessLevel, pastClasses, availability);
        // Step 1: Get streaming response and buffer it
        /*return ollamaService.streamAiResponse(systemPrompt, userFormattedPrompt)
                .reduce(new StringBuilder(), StringBuilder::append) // Collect all tokens
                .flatMapMany(fullResponse -> {
                    log.info("Complete AI response received");
                    // Step 2: Clean the response
                    String cleanResponse = fullResponse.toString().replaceAll("```json\\n?", "").replaceAll("```", "").trim();
                    log.debug("Cleaned response: {}", cleanResponse);

                    try {
                        // Step 3: Parse JSON array
                        List<ClassRecommendationDTO> recommendations = objectMapper.readValue(cleanResponse, new TypeReference<List<ClassRecommendationDTO>>() {
                        });
                        log.info("Parsed {} recommendations", recommendations.size());

                        // Step 4: Stream items one by one with delay
                        return Flux.fromIterable(recommendations).delayElements(Duration.ofMillis(500)) // 500ms between items
                                .doOnNext(rec -> log.info("Emitting recommendation: {}", rec.getClassName()));


                    } catch (Exception e) {
                        log.error("Failed to parse AI response: {}", cleanResponse, e);
                        return Flux.error(new RuntimeException("Failed to parse AI response", e));
                    }
                });*/
        return ollamaService.getRecommendationsStream(systemPrompt, userFormattedPrompt);
    }
}
