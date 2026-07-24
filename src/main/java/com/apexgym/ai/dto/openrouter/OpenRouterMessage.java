package com.apexgym.ai.dto.openrouter;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpenRouterMessage(
        String role,
        String content,
        String name,
        List<ToolCall> tool_calls,
        String tool_call_id
) {
    public OpenRouterMessage(String role, String content) {
        this(role, content, null, null, null);
    }

    public record ToolCall(
            String id,
            String type,
            FunctionCall function
    ) {
    }

    public record FunctionCall(
            String name,
            String arguments
    ) {
    }
}
