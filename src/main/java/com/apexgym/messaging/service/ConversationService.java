package com.apexgym.messaging.service;

import com.apexgym.auth.persistence.Role;
import com.apexgym.auth.persistence.User;
import com.apexgym.auth.persistence.UserRepository;
import com.apexgym.messaging.dto.ConversationDTO;
import com.apexgym.messaging.dto.ConversationListResponseDTO;
import com.apexgym.messaging.mapper.ChatMapper;
import com.apexgym.messaging.persistence.ChatMessageRepository;
import com.apexgym.messaging.persistence.Conversation;
import com.apexgym.messaging.persistence.ConversationRepository;
import com.apexgym.messaging.persistence.MessageStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;

    /**
     * Resolves or creates a 1-on-1 conversation between a USER and a TRAINER.
     * Enforces that pairing MUST be strictly between a USER and a TRAINER.
     */
    @Transactional
    public ConversationDTO getOrCreateConversation(String requesterEmail, Long targetUserId) {
        User requester = getUserByEmail(requesterEmail);
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found with ID: " + targetUserId));

        User user;
        User trainer;

        if (requester.getRole() == Role.USER && targetUser.getRole() == Role.TRAINER) {
            user = requester;
            trainer = targetUser;
        } else if (requester.getRole() == Role.TRAINER && targetUser.getRole() == Role.USER) {
            user = targetUser;
            trainer = requester;
        } else {
            throw new IllegalArgumentException("Conversations are only allowed between a Client (USER) and a Trainer (TRAINER).");
        }

        Conversation conversation = conversationRepository
                .findByUserIdAndTrainerId(user.getId(), trainer.getId())
                .orElseGet(() -> {
                    Conversation newConversation = Conversation.builder()
                            .user(user)
                            .trainer(trainer)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return conversationRepository.save(newConversation);
                });

        long unreadCount = chatMessageRepository.countByConversationIdAndSenderIdNotAndStatusNot(
                conversation.getId(), requester.getId(), MessageStatus.READ
        );

        return chatMapper.toConversationDTO(conversation, requester.getId(), unreadCount);
    }

    /**
     * Retrieves all active conversations for the authenticated user, ordered by latest message.
     */
    @Transactional(readOnly = true)
    public ConversationListResponseDTO getConversationsForCurrentUser(String requesterEmail) {
        User requester = getUserByEmail(requesterEmail);

        List<Conversation> conversations = (requester.getRole() == Role.TRAINER)
                ? conversationRepository.findByTrainerIdOrderByLastMessageAtDesc(requester.getId())
                : conversationRepository.findByUserIdOrderByLastMessageAtDesc(requester.getId());

        List<ConversationDTO> dtos = conversations.stream()
                .map(conv -> {
                    long unreadCount = chatMessageRepository.countByConversationIdAndSenderIdNotAndStatusNot(
                            conv.getId(), requester.getId(), MessageStatus.READ
                    );
                    return chatMapper.toConversationDTO(conv, requester.getId(), unreadCount);
                })
                .toList();

        long totalUnread = dtos.stream().mapToLong(ConversationDTO::unreadCount).sum();

        return new ConversationListResponseDTO(dtos, totalUnread);
    }

    /**
     * Bulk updates all messages in a conversation sent by the other party to READ status.
     */
    @Transactional
    public void markConversationAsRead(Long conversationId, String requesterEmail) {
        User requester = getUserByEmail(requesterEmail);
        Conversation conversation = getConversationAndVerifyParticipant(conversationId, requester.getId());

        chatMessageRepository.updateStatusForConversation(conversation.getId(), requester.getId(), MessageStatus.READ);
    }

    /**
     * Internal helper to verify participation rights.
     */
    public Conversation getConversationAndVerifyParticipant(Long conversationId, Long userId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found with ID: " + conversationId));

        if (!conversation.getUser().getId().equals(userId) && !conversation.getTrainer().getId().equals(userId)) {
            throw new AccessDeniedException("You are not a participant in this conversation.");
        }

        return conversation;
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }
}