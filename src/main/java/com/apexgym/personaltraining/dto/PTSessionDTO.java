package com.apexgym.personaltraining.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record PTSessionDTO(
        Long id,
        Long trainerId,
        String trainerName,
        Long userId,
        String userName,
        LocalDateTime scheduledAt,
        Integer durationMinutes,
        String status,
        String goalFocus
) {}