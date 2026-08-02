package com.apexgym.messaging.event;

import com.apexgym.messaging.dto.ChatMessageDTO;

public record MessageSentEvent(
        Long conversationId,
        Long recipientId,
        ChatMessageDTO message
) {}