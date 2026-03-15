package com.apexgym.booking.event;

import com.apexgym.auth.persistence.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ClassBookedEvent extends ApplicationEvent {
    private final User user;
    private final String className;

    public ClassBookedEvent(Object source, User user, String className) {
        super(source);
        this.user = user;
        this.className = className;
    }
}
