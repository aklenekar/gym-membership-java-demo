package com.apexgym.messaging.dto;

import java.time.LocalDateTime;

public record ConversationDTO(
    Long id,
    String participantName,
    String participantAvatar,
    String participantRole,
    String lastMessagePreview,
    LocalDateTime lastMessageAt,
    Integer unreadCount
) {}
