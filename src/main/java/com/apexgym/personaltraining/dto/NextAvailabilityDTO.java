package com.apexgym.personaltraining.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record NextAvailabilityDTO(
        Long trainerId,
        String trainerName,
        String initials,
        String specialty,
        String imageUrl,
        LocalDateTime nextSlot
) {}