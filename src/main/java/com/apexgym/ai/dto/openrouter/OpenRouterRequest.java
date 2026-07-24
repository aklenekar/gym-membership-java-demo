package com.apexgym.ai.dto.openrouter;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenRouterRequest(
        String model,
        List<OpenRouterMessage> messages,
        List<Map<String, Object>> tools,
        Double temperature,
        Boolean stream
) {
    public OpenRouterRequest(String model, List<OpenRouterMessage> messages, Double temperature, Boolean stream) {
        this(model, messages, null, temperature, stream);
    }
}
