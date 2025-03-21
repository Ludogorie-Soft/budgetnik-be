package com.ludogorieSoft.budgetnik.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
@Setter
public class OnSendMessageEvent extends ApplicationEvent {
    private final String email;
    private final UUID messageId;

    public OnSendMessageEvent(String email, UUID messageId) {
        super(email);
        this.email = email;
        this.messageId = messageId;
    }
}
