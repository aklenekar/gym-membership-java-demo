package com.apexgym.messaging.web;

import com.apexgym.messaging.dto.ReadReceiptDTO;
import com.apexgym.messaging.dto.SendMessageRequest;
import com.apexgym.messaging.dto.TypingIndicatorDTO;
import com.apexgym.messaging.service.ChatMessageService;
import com.apexgym.messaging.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatMessageService chatMessageService;
    private final ConversationService conversationService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Destination: /app/chat.send
     * Persists and broadcasts messages.
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload SendMessageRequest request, Principal principal) {
        chatMessageService.sendMessage(principal.getName(), request);
    }

    /**
     * Destination: /app/chat.typing
     * Broadcasts ephemeral typing indicators to active topic subscribers.
     */
    @MessageMapping("/chat.typing")
    public void sendTyping(@Payload TypingIndicatorDTO indicator) {
        messagingTemplate.convertAndSend(
                "/topic/conversation/" + indicator.conversationId() + "/typing",
                indicator
        );
    }

    /**
     * Destination: /app/chat.read
     * Marks thread as read and broadcasts receipt to conversation participants.
     */
    @MessageMapping("/chat.read")
    public void markAsRead(@Payload ReadReceiptDTO readReceipt, Principal principal) {
        conversationService.markConversationAsRead(readReceipt.conversationId(), principal.getName());

        messagingTemplate.convertAndSend(
                "/topic/conversation/" + readReceipt.conversationId() + "/read",
                readReceipt
        );
    }
}
