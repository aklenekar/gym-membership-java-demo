package com.apexgym.personaltraining.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record OpenSlotDTO(LocalDateTime start, LocalDateTime end) {}