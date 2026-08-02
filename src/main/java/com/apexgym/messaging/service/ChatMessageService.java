package com.apexgym.messaging.service;

import com.apexgym.auth.persistence.Role;
import com.apexgym.auth.persistence.User;
import com.apexgym.auth.persistence.UserRepository;
import com.apexgym.messaging.dto.ChatMessageDTO;
import com.apexgym.messaging.dto.MessageHistoryResponseDTO;
import com.apexgym.messaging.dto.SendMessageRequest;
import com.apexgym.messaging.mapper.ChatMapper;
import com.apexgym.messaging.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final ConversationService conversationService;
    private final ChatMapper chatMapper;
/*
    private final SimpMessagingTemplate messagingTemplate;
*/
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Persists message, updates conversation lastMessage state, pushes via WebSocket,
     * and publishes an internal application event.
     */
    @Transactional
    public ChatMessageDTO sendMessage(String senderEmail, SendMessageRequest request) {
        User sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found with email: " + senderEmail));

        Conversation conversation;

        if (request.conversationId() != null) {
            conversation = conversationService.getConversationAndVerifyParticipant(request.conversationId(), sender.getId());
        } else if (request.recipientId() != null) {
            // Resolve/Create dynamically via request fallback
            var convDto = conversationService.getOrCreateConversation(senderEmail, request.recipientId());
            conversation = conversationRepository.findById(convDto.id())
                    .orElseThrow(() -> new IllegalStateException("Failed to locate conversation after creation."));
        } else {
            throw new IllegalArgumentException("Either conversationId or recipientId must be provided.");
        }

        LocalDateTime now = LocalDateTime.now();

        ChatMessage message = ChatMessage.builder()
                .conversation(conversation)
                .sender(sender)
                .content(request.content().trim())
                .sentAt(now)
                .status(MessageStatus.SENT)
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        // Update conversation metadata preview
        String preview = request.content().length() > 100
                ? request.content().substring(0, 97) + "..."
                : request.content();
        conversation.setLastMessageAt(now);
        conversation.setLastMessagePreview(preview);
        conversationRepository.save(conversation);

        ChatMessageDTO messageDTO = chatMapper.toChatMessageDTO(savedMessage, sender.getId());

        /*// Dynamic target recipient determination
        Long recipientId = conversation.getUser().getId().equals(sender.getId())
                ? conversation.getTrainer().getId()
                : conversation.getUser().getId();

        // 1. Broadcast message to active thread subscribers
        messagingTemplate.convertAndSend("/topic/conversation/" + conversation.getId(), messageDTO);

        // 2. Broadcast realtime update event to specific recipient channel
        messagingTemplate.convertAndSendToUser(
                recipientId.toString(),
                "/queue/notifications",
                messageDTO
        );

        // 3. Publish application event for decoupled listeners (Phase 2 badges / emails / push)
        eventPublisher.publishEvent(new MessageSentEvent(conversation.getId(), recipientId, messageDTO));*/

        return messageDTO;
    }

    /**
     * Fetches paginated chat history with authorization verification.
     */
    @Transactional(readOnly = true)
    public MessageHistoryResponseDTO getMessageHistory(Long conversationId, String requesterEmail, Pageable pageable) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + requesterEmail));

        // Enforce access control check
        conversationService.getConversationAndVerifyParticipant(conversationId, requester.getId());

        Page<ChatMessage> messagePage = chatMessageRepository.findByConversationIdOrderBySentAtAsc(conversationId, pageable);

        List<ChatMessageDTO> dtos = messagePage.getContent().stream()
                .map(msg -> chatMapper.toChatMessageDTO(msg, requester.getId()))
                .toList();

        return new MessageHistoryResponseDTO(
                dtos,
                messagePage.hasNext(),
                messagePage.getNumber()
        );
    }

    /**
     * Calculates total aggregated unread count across all user conversations.
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));

        List<Conversation> conversations = (user.getRole() == Role.TRAINER)
                ? conversationRepository.findByTrainerIdOrderByLastMessageAtDesc(user.getId())
                : conversationRepository.findByUserIdOrderByLastMessageAtDesc(user.getId());

        return conversations.stream()
                .mapToLong(c -> chatMessageRepository.countByConversationIdAndSenderIdNotAndStatusNot(
                        c.getId(), user.getId(), MessageStatus.READ
                ))
                .sum();
    }
}