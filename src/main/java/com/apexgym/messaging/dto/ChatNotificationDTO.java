package com.apexgym.messaging.dto;

import java.time.LocalDateTime;

public record ChatNotificationDTO(
        Long conversationId,
        Long senderId,
        String senderName,
        String messagePreview,
        long unreadCount,
        LocalDateTime timestamp
) {}