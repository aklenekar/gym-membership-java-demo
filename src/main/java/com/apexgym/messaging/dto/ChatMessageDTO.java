package com.apexgym.messaging.dto;

import java.time.LocalDateTime;

public record ChatMessageDTO(
    Long id,
    Long conversationId,
    Long senderId,
    String senderName,
    String content,
    LocalDateTime sentAt,
    String status,
    boolean isOwnMessage
) {
}
