package com.apexgym.ai.infrastructure.gemini;

import com.apexgym.ai.dto.ClassRecommendationDTO;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

@Slf4j
public final class GeminiJsonUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private GeminiJsonUtil() {}

    public static List<ClassRecommendationDTO> parseClassRecommendations(String rawText) {
        String cleaned = clean(rawText);
        try {
            return MAPPER.readValue(cleaned, new TypeReference<List<ClassRecommendationDTO>>() {});
        } catch (Exception e) {
            log.error("Failed to parse Gemini response: {}", cleaned, e);
            return Collections.emptyList();
        }
    }

    public static String clean(String rawText) {
        return rawText == null ? "" : rawText.replaceAll("```json\\n?", "").replaceAll("```", "").trim();
    }
}
