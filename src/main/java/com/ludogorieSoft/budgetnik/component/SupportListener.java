package com.ludogorieSoft.budgetnik.component;

import com.ludogorieSoft.budgetnik.event.OnSupportEvent;
import com.ludogorieSoft.budgetnik.model.ContactForm;
import com.ludogorieSoft.budgetnik.service.impl.SupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SupportListener implements ApplicationListener<OnSupportEvent> {

  private final SupportService supportService;
  private final JavaMailSender mailSender;

  @Value("${support.email}")
  private String supportEmail;

  @Override
  public void onApplicationEvent(OnSupportEvent event) {

    ContactForm contactForm = supportService.findById(event.getContactFormId());

    String recipientAddress = supportEmail;
    String subject = "BUDGETникът - Support";

    String message =
        "From: "
            + contactForm.getEmail()
            + "\n\n"
            + contactForm.getTitle()
            + "\n\n"
            + contactForm.getMessage();

    SimpleMailMessage email = new SimpleMailMessage();
    email.setFrom("BUDGETникът");
    email.setReplyTo("BUDGETникът");
    email.setTo(recipientAddress);
    email.setSubject(subject);
    email.setText(message);

    mailSender.send(email);
  }
}
