package com.apexgym.personaltraining.dto;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record SessionNoteDTO(Long id, Long sessionId, String content, LocalDateTime createdAt) {}