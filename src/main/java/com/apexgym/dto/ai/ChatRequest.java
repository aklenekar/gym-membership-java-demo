package com.apexgym.dto.ai;

import lombok.Builder;

@Builder
public record ChatRequest(
    String message,
    String conversationId
) {}
