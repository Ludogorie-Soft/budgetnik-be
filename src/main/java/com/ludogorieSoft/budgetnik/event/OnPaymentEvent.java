package com.ludogorieSoft.budgetnik.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnPaymentEvent extends ApplicationEvent {

  private String email;

  public OnPaymentEvent(String email) {
    super(email);
    this.email = email;
  }
}
