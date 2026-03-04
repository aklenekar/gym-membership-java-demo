package com.apexgym.dto.ai;

import lombok.Data;
import java.util.List;

@Data
public class ClassRecommendationRequest {
    private String goals;
    private String fitnessLevel;
    private List<String> pastClasses;
    private String availability;
}

