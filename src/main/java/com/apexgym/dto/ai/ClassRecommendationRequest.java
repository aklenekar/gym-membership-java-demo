package com.apexgym.dto.ai;

import lombok.Builder;
import java.util.List;

@Builder
public record ClassRecommendationRequest(
    String goals,
    String fitnessLevel,
    List<String> pastClasses,
    String availability
) {}
