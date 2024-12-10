package com.ludogorieSoft.budgetnik.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class OnConfirmRegistrationEvent extends ApplicationEvent {

  private String email;

  public OnConfirmRegistrationEvent(String email) {
    super(email);

    this.email = email;
  }
}
