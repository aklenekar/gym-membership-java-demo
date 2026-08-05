package com.apexgym.admin.dto;

import com.apexgym.admin.persistence.PricingFeatures;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record PricingDTO (
    String name,
    Integer price,
    Integer annualPrice,
    Boolean mostFeatured,
    List<PricingFeatures> features
) {
}
