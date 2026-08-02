package com.apexgym.messaging.dto;

import java.util.List;

public record MessageHistoryResponseDTO(List<ChatMessageDTO> messages, boolean hasMore, int nextCursor) {
}
