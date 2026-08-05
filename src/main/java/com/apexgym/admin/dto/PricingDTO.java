package com.apexgym.admin.dto;

import com.apexgym.profile.persistence.PricingFeatures;
import lombok.Builder;

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
