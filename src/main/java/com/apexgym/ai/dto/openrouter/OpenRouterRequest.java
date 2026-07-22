package com.apexgym.ai.dto.openrouter;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenRouterRequest(
        String model,
        List<OpenRouterMessage> messages,
        Double temperature,
        Boolean stream
) {}
