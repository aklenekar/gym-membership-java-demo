package com.apexgym.admin.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record PricingResponseDTO(List<PricingDTO> pricing) {
}
