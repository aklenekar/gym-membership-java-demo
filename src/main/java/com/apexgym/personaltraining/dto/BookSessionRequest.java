package com.apexgym.personaltraining.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record BookSessionRequest(
        @NotNull Long trainerId,
        @NotNull LocalDateTime scheduledAt,
        Integer durationMinutes,
        String goalFocus
) {}