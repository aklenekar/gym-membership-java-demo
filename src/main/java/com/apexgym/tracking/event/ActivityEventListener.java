package com.apexgym.tracking.event;

import com.apexgym.entity.Activity;
import com.apexgym.entity.ActivityType;
import com.apexgym.booking.event.ClassBookedEvent;
import com.apexgym.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityEventListener {

    private final ActivityRepository activityRepository;

    @EventListener
    public void handleClassBookedEvent(ClassBookedEvent event) {
        Activity activity = Activity.builder()
                .user(event.getUser())
                .type(ActivityType.CLASS_ATTENDED)
                .title("Booked " + event.getClassName())
                .icon("📅")
                .build();
        activityRepository.save(activity);
    }
}
