package com.apexgym.ai.dto.openrouter;

import com.apexgym.ai.dto.AiResponse;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenRouterResponse(String id, List<Choice> choices) implements AiResponse {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Choice(OpenRouterMessage message, OpenRouterMessage delta, String finish_reason) {}
}