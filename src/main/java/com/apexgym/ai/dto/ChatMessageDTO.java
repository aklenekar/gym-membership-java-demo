package com.apexgym.ai.dto;

public record ChatMessageDTO(
        String role,
        String content
) {
    public static ChatMessageDTO userMessage(String content) {
        return new ChatMessageDTO("user", content);
    }

    public static ChatMessageDTO assistantMessage(String content) {
        return new ChatMessageDTO("assistant", content);
    }
}
