package com.apexgym.messaging.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for sending a message.
 *
 * Fields:
 * - conversationId: nullable when creating a new conversation
 * - recipientId: used when conversationId is null (start new conversation with this recipient)
 * - content: message body (must not be blank)
 */
public record SendMessageRequest(
		Long conversationId,
		Long recipientId,
		@NotBlank String content
) {
}
