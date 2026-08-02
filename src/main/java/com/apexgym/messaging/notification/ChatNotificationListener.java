package com.apexgym.messaging.notification;

import com.apexgym.auth.persistence.User;
import com.apexgym.auth.persistence.UserRepository;
import com.apexgym.messaging.dto.ChatNotificationDTO;
import com.apexgym.messaging.event.MessageSentEvent;
import com.apexgym.messaging.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatNotificationListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService chatMessageService;
    private final UserRepository userRepository;

    /**
     * Listens for MessageSentEvents and pushes real-time notification updates
     * directly to the target user's private WS queue.
     */
    @Async
    @EventListener
    @Transactional(readOnly = true)
    public void handleMessageSentEvent(MessageSentEvent event) {
        try {
            User recipient = userRepository.findById(event.recipientId())
                    .orElseThrow(() -> new IllegalArgumentException("Recipient not found with ID: " + event.recipientId()));

            // Calculate overall unread count for recipient across all their active threads
            long updatedTotalUnread = chatMessageService.getUnreadCount(recipient.getEmail());

            ChatNotificationDTO notification = new ChatNotificationDTO(
                    event.conversationId(),
                    event.message().senderId(),
                    event.message().senderName(),
                    event.message().content(),
                    updatedTotalUnread,
                    event.message().sentAt()
            );

            // Push payload to /user/{recipientId}/queue/notifications via STOMP user destination mapping
            messagingTemplate.convertAndSendToUser(
                    event.recipientId().toString(),
                    "/queue/notifications",
                    notification
            );

            log.debug("Pushed unread chat notification to user ID: {}", event.recipientId());
        } catch (Exception e) {
            log.error("Failed to process ChatNotificationListener for event: {}", event, e);
        }
    }
}