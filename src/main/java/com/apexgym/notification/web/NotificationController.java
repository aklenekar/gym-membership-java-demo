package com.apexgym.notification.web;

import com.apexgym.notification.dto.NotificationDTO;
import com.apexgym.notification.service.NotificationService;
import com.apexgym.shared.CommonHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final CommonHelper commonHelper;

    /**
     * GET /notifications
     * Returns all notifications for the authenticated user (role-aware).
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getNotifications() {
        String email = commonHelper.getCurrentUserEmail();
        List<NotificationDTO> response = notificationService.getNotifications(email);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /notifications/unread-count
     * Returns notification unread count for the authenticated user (role-aware).
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Long> getNotificationUnreadCount() {
        String email = commonHelper.getCurrentUserEmail();
        long response = notificationService.notificationCount(email);
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /messages/conversations/{id}/read
     * Marks all messages within the given conversation as READ for the current user.
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable("id") Long notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    /**
     * PUT /messages/conversations/{id}/read
     * Marks all messages within the given conversation as READ for the current user.
     */
    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        String email = commonHelper.getCurrentUserEmail();
        notificationService.markAllNotifications(email);
        return ResponseEntity.noContent().build();
    }
}
