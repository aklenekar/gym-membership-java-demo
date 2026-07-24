package com.apexgym.ai.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    // Fetch the most recent N messages descending to apply limit, then sort chronologically in service
    @Query("SELECT cm FROM ChatMessage cm WHERE cm.userEmail = :email ORDER BY cm.createdAt DESC")
    List<ChatMessage> findRecentMessagesByUserEmail(@Param("email") String email, Pageable pageable);

    void deleteByUserEmail(String email);
}