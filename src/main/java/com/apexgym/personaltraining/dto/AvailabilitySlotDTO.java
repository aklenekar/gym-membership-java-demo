package com.apexgym.personaltraining.dto;

import lombok.Builder;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Builder
public record AvailabilitySlotDTO(
        Long id,
        DayOfWeek dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {}