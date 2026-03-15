package com.apexgym.dto;

import com.apexgym.ai.dto.FitnessClass;

import java.util.List;

public record RecommendationResponse(List<FitnessClass> recommendations) {
}
