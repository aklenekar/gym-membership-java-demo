package com.apexgym.profile.event;

import com.apexgym.auth.persistence.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PlanChangeEvent extends ApplicationEvent {
    private final User user;
    private final String planName;

    public PlanChangeEvent(Object source, User user, String planName) {
        super(source);
        this.user = user;
        this.planName = planName;
    }
}
