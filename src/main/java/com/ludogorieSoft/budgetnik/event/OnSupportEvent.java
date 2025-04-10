package com.ludogorieSoft.budgetnik.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
@Setter
public class OnSupportEvent extends ApplicationEvent {

  private UUID contactFormId;

  public OnSupportEvent(UUID contactFormId) {
    super(contactFormId);
    this.contactFormId = contactFormId;
  }
}
