package com.apexgym.dto.ai;

import lombok.Builder;
import java.util.List;

@Builder
public record ClassRecommendationDTO(
    String className,
    String reasoning,
    List<String> benefits,
    Integer matchPercentage
) {}
