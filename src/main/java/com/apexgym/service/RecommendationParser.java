package com.apexgym.service;

import com.apexgym.dto.FitnessClass;
import com.apexgym.dto.RecommendationResponse;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

@Slf4j
public class RecommendationParser {

    public static List<FitnessClass> convertToJson(String ollamaRawText) throws Exception {
        try {
            ollamaRawText = ollamaRawText.replaceAll("`", "").replace("json", "");
            System.out.println(ollamaRawText);
            ObjectMapper objectMapper = new ObjectMapper();

            // 1. Map the JSON to the wrapper record
            RecommendationResponse response = objectMapper.readValue(
                    ollamaRawText,
                    RecommendationResponse.class
            );

            // 2. Extract the List
            return response.recommendations();
        } catch (Exception e) {
            log.error("Exception occurred while parsing {}", e.getMessage());
            return Collections.emptyList();
        }
    }
}
