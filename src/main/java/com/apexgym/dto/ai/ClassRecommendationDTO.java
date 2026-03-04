package com.apexgym.dto.ai;

import lombok.Data;
import java.util.List;

@Data
public class ClassRecommendationDTO {
    private String className;
    private String reasoning;
    private List<String> benefits;
    private Integer matchPercentage;
}
