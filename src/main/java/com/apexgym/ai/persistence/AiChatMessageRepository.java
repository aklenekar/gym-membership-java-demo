package com.apexgym.ai.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AiChatMessageRepository extends JpaRepository<AIChatMessage, Long> {

    // Fetch the most recent N messages descending to apply limit, then sort chronologically in service
    @Query("SELECT cm FROM AIChatMessage cm WHERE cm.userEmail = :email ORDER BY cm.createdAt DESC")
    List<AIChatMessage> findRecentMessagesByUserEmail(@Param("email") String email, Pageable pageable);

    void deleteByUserEmail(String email);
}