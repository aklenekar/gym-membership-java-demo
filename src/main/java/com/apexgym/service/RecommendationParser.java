package com.apexgym.service;

import com.apexgym.dto.FitnessClass;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecommendationParser {

    public static List<FitnessClass> convertToJson(String ollamaRawText) throws Exception {
        List<FitnessClass> classes = new ArrayList<>();

        // Regex to find: 1. **Title**: Description
        // Group 1: Title, Group 2: Description
        Pattern pattern = Pattern.compile("\\d+\\.\\s+\\*\\*(.*?)\\*\\*:\\s+(.*)");
        Matcher matcher = pattern.matcher(ollamaRawText);

        while (matcher.find()) {
            String title = matcher.group(1).trim();
            String description = matcher.group(2).trim();
            classes.add(new FitnessClass(title, description));
        }
        return classes;
    }
}
