package com.apexgym.messaging.dto;

import java.util.List;

public record ConversationListResponseDTO(List<ConversationDTO> conversations, long totalUnreadCount) {
}
