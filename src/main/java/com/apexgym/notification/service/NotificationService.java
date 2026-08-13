package com.apexgym.notification.service;

import com.apexgym.auth.persistence.User;
import com.apexgym.notification.dto.NotificationDTO;
import com.apexgym.notification.persistence.Notification;
import com.apexgym.notification.persistence.NotificationRepository;
import com.apexgym.notification.persistence.NotificationType;
import com.apexgym.profile.dto.UserDTO;
import com.apexgym.profile.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public void notify(User user, String title, String body, NotificationType type) {
        notificationRepository.save(Notification.builder()
                .user(user).title(title).body(body).type(type).build());
        // optional: push via existing STOMP infra -> /user/{id}/queue/notifications
    }

    public List<NotificationDTO> getNotifications(String email) {
        UserDTO user = userService.getUserDetails(email);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.id()).stream()
                .map(notification -> NotificationDTO.builder()
                        .id(notification.getId())
                        .title(notification.getTitle())
                        .type(notification.getType().name())
                        .isRead(notification.getIsRead())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Long notificationCount(String email) {
        UserDTO user = userService.getUserDetails(email);
        return notificationRepository.countByUserIdAndIsReadFalse(user.id());
    }

    @Transactional
    public void markAllNotifications(String email) {
        UserDTO user = userService.getUserDetails(email);
        notificationRepository.markAllRead(user.id());
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId) {
        notificationRepository.markNotificationRead(notificationId);
    }
}
