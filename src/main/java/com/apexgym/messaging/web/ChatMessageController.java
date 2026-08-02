package com.apexgym.messaging.web;

import com.apexgym.messaging.dto.ChatMessageDTO;
import com.apexgym.messaging.dto.MessageHistoryResponseDTO;
import com.apexgym.messaging.dto.SendMessageRequest;
import com.apexgym.messaging.service.ChatMessageService;
import com.apexgym.shared.CommonHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/messages/conversations")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final CommonHelper commonHelper;

    /**
     * GET /messages/conversations/{conversationId}/messages?page=0&size=30
     * Fetches paginated history for a conversation after verifying user participation.
     */
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<MessageHistoryResponseDTO> getMessageHistory(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size
    ) {
        String email = commonHelper.getCurrentUserEmail();
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").ascending());
        MessageHistoryResponseDTO history = chatMessageService.getMessageHistory(conversationId, email, pageable);
        return ResponseEntity.ok(history);
    }

    /**
     * POST /messages/conversations/messages
     * HTTP POST fallback for sending messages outside WebSockets.
     */
    @PostMapping("/messages")
    public ResponseEntity<ChatMessageDTO> sendMessage(@RequestBody SendMessageRequest request) {
        String email = commonHelper.getCurrentUserEmail();
        ChatMessageDTO sentMessage = chatMessageService.sendMessage(email, request);
        return ResponseEntity.ok(sentMessage);
    }

    /**
     * GET /messages/conversations/unread-count
     * Utility endpoint for global navbar unread badges.
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        String email = commonHelper.getCurrentUserEmail();
        long count = chatMessageService.getUnreadCount(email);
        return ResponseEntity.ok(count);
    }
}
