package com.apexgym.messaging.web;

import com.apexgym.messaging.dto.ConversationDTO;
import com.apexgym.messaging.dto.ConversationListResponseDTO;
import com.apexgym.messaging.dto.SendMessageRequest;
import com.apexgym.messaging.dto.StartConversationRequest;
import com.apexgym.messaging.service.ConversationService;
import com.apexgym.shared.CommonHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final CommonHelper commonHelper;

    /**
     * GET /messages/conversations
     * Returns all active conversations for the authenticated user (role-aware).
     */
    @GetMapping
    public ResponseEntity<ConversationListResponseDTO> getConversations() {
        String email = commonHelper.getCurrentUserEmail();
        ConversationListResponseDTO response = conversationService.getConversationsForCurrentUser(email);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /messages/conversations/start
     * Gets or creates a conversation thread with the target participant ID.
     */
    @PostMapping("/start")
    public ResponseEntity<ConversationDTO> startConversation(@RequestBody StartConversationRequest request) {
        String email = commonHelper.getCurrentUserEmail();
        ConversationDTO conversation = conversationService.getOrCreateConversation(email, request.targetUserId());
        return ResponseEntity.ok(conversation);
    }

    /**
     * PUT /messages/conversations/{id}/read
     * Marks all messages within the given conversation as READ for the current user.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable("id") Long conversationId) {
        String email = commonHelper.getCurrentUserEmail();
        conversationService.markConversationAsRead(conversationId, email);
        return ResponseEntity.noContent().build();
    }
}