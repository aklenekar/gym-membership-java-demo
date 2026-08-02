package com.apexgym.messaging.dto;

public record TypingIndicatorDTO(
        Long conversationId,
        Long senderId,
        String senderName,
        boolean isTyping
) {}