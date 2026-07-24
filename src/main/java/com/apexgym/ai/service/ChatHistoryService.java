package com.apexgym.ai.service;

import com.apexgym.ai.persistence.ChatMessage;
import com.apexgym.ai.dto.ChatMessageDTO;
import com.apexgym.ai.persistence.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatHistoryService {

    private final ChatMessageRepository repository;

    /**
     * Retrieves the latest N messages for a user, sorted in chronological order (oldest to newest).
     */
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getRecentHistory(String userEmail, int limit) {
        List<ChatMessage> recent = repository.findRecentMessagesByUserEmail(userEmail, PageRequest.of(0, limit));

        // Reverse so the conversation flows naturally from top to bottom
        Collections.reverse(recent);

        return recent.stream()
                .map(msg -> new ChatMessageDTO(msg.getRole(), msg.getContent()))
                .toList();
    }

    /**
     * Saves a user query message.
     */
    @Transactional
    public ChatMessage saveUserMessage(String userEmail, String content) {
        ChatMessage message = ChatMessage.builder()
                .userEmail(userEmail)
                .role("user")
                .content(content)
                .build();
        return repository.save(message);
    }

    /**
     * Saves the final assistant response after streaming completes.
     */
    @Transactional
    public ChatMessage saveAssistantMessage(String userEmail, String content) {
        ChatMessage message = ChatMessage.builder()
                .userEmail(userEmail)
                .role("assistant")
                .content(content)
                .build();
        return repository.save(message);
    }

    /**
     * Clears chat history for a given user.
     */
    @Transactional
    public void clearHistory(String userEmail) {
        repository.deleteByUserEmail(userEmail);
        log.info("Cleared chat history for user: {}", userEmail);
    }
}
