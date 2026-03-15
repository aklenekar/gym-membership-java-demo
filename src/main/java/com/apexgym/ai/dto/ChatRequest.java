package com.apexgym.ai.dto;

import lombok.Builder;

@Builder
public record ChatRequest(
    String message,
    String conversationId
) {}
