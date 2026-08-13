package com.apexgym.notification.dto;

import lombok.Builder;

@Builder
public record NotificationDTO(Long id, boolean isRead, String type, String title, String body ) {
}
