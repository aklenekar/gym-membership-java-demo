package com.apexgym.ai.dto.openrouter;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenRouterMessage(String role, String content) {}
