package com.apexgym.messaging.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByUserIdAndTrainerId(Long userId, Long trainerId);

    List<Conversation> findByUserIdOrderByLastMessageAtDesc(Long userId);

    List<Conversation> findByTrainerIdOrderByLastMessageAtDesc(Long trainerId);

    @Query("UPDATE Conversation c SET c.lastMessageAt = CURRENT_TIMESTAMP WHERE c.id = :id")
    void updateLastMessageAt(Long id);
}
