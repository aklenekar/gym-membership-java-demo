package com.apexgym.messaging.mapper;

import com.apexgym.auth.persistence.User;
import com.apexgym.messaging.dto.ChatMessageDTO;
import com.apexgym.messaging.dto.ConversationDTO;
import com.apexgym.messaging.persistence.ChatMessage;
import com.apexgym.messaging.persistence.Conversation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMapper {

    @Mapping(target = "id", source = "conversation.id")
    @Mapping(target = "participantName", expression = "java(resolveParticipantName(conversation, requesterId))")
    @Mapping(target = "participantAvatar", expression = "java(resolveParticipantAvatar(conversation, requesterId))")
    @Mapping(target = "participantRole", expression = "java(resolveParticipantRole(conversation, requesterId))")
    @Mapping(target = "lastMessagePreview", source = "conversation.lastMessagePreview")
    @Mapping(target = "lastMessageAt", source = "conversation.lastMessageAt")
    @Mapping(target = "unreadCount", source = "unreadCount")
    ConversationDTO toConversationDTO(Conversation conversation, Long requesterId, long unreadCount);

    @Mapping(target = "id", source = "message.id")
    @Mapping(target = "conversationId", source = "message.conversation.id")
    @Mapping(target = "senderId", source = "message.sender.id")
    @Mapping(target = "senderName", expression = "java(resolveSenderName(message.getSender()))")
    @Mapping(target = "content", source = "message.content")
    @Mapping(target = "sentAt", source = "message.sentAt")
    @Mapping(target = "status", source = "message.status")
    @Mapping(target = "isOwnMessage", expression = "java(message.getSender().getId().equals(requesterId))")
    ChatMessageDTO toChatMessageDTO(ChatMessage message, Long requesterId);

    // --- Helper Methods ---

    default User resolveParticipant(Conversation conversation, Long requesterId) {
        if (conversation.getUser().getId().equals(requesterId)) {
            return conversation.getUser();
        }
        return conversation.getUser();
    }

    default String resolveParticipantName(Conversation conversation, Long requesterId) {
        User other = resolveParticipant(conversation, requesterId);
        return resolveSenderName(other);
    }

    default String resolveParticipantAvatar(Conversation conversation, Long requesterId) {
        User other = resolveParticipant(conversation, requesterId);
        return other.getLastName(); // Or initials fallback logic if null
    }

    default String resolveParticipantRole(Conversation conversation, Long requesterId) {
        User other = resolveParticipant(conversation, requesterId);
        return other.getRole().name();
    }

    default String resolveSenderName(User sender) {
        if (sender.getFirstName() != null && sender.getLastName() != null) {
            return sender.getFirstName() + " " + sender.getLastName();
        }
        return sender.getEmail();
    }
}
