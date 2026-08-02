package com.apexgym.messaging.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findByConversationIdOrderBySentAtAsc(Long conversationId, Pageable pageable);

    long countByConversationIdAndSenderIdNotAndStatusNot(Long conversationId, Long userId, MessageStatus status); // unread count

    @Modifying
    @Query("UPDATE ChatMessage cm SET cm.status = :status WHERE cm.conversation.id = :conversationId AND cm.sender.id = :readerId")
    void updateStatusForConversation(Long conversationId, Long readerId, MessageStatus status);
}
