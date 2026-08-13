package com.apexgym.tracking.event;

import com.apexgym.booking.event.ClassBookedEvent;
import com.apexgym.booking.event.ClassCancelledEvent;
import com.apexgym.notification.persistence.Notification;
import com.apexgym.notification.persistence.NotificationRepository;
import com.apexgym.notification.persistence.NotificationType;
import com.apexgym.profile.event.PlanChangeEvent;
import com.apexgym.profile.persistence.Activity;
import com.apexgym.profile.persistence.ActivityType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleClassBookedNotification(ClassBookedEvent event) {
        Notification notification = Notification.builder()
                .user(event.getUser())
                .type(NotificationType.CLASS_BOOKED)
                .title("Booked " + event.getClassName())
                .body("📅")
                .build();
        notificationRepository.save(notification);
    }

    @EventListener
    public void handleClassCancelledNotification(ClassCancelledEvent event) {
        Notification notification = Notification.builder()
                .user(event.getUser())
                .type(NotificationType.CLASS_CANCELLED)
                .title("Cancelled " + event.getClassName())
                .body("📅")
                .build();
        notificationRepository.save(notification);
    }

    @EventListener
    public void handlePLanChangeNotification(PlanChangeEvent event) {
        Notification notification = Notification.builder()
                .user(event.getUser())
                .type(NotificationType.MEMBERSHIP_UPGRADED)
                .title("New plan available - " + event.getPlanName())
                .body("📅")
                .build();
        notificationRepository.save(notification);
    }
}
