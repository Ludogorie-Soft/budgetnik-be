package com.ludogorieSoft.budgetnik.controller;

import com.ludogorieSoft.budgetnik.dto.request.ContactFormRequestDto;
import com.ludogorieSoft.budgetnik.event.OnSupportEvent;
import com.ludogorieSoft.budgetnik.model.ContactForm;
import com.ludogorieSoft.budgetnik.service.impl.SupportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contact-us")
public class SupportController {

  private final SupportService supportService;
  private final ApplicationEventPublisher applicationEventPublisher;

  @PostMapping
  public ResponseEntity<ContactForm> createMessage(
      @Valid @RequestBody ContactFormRequestDto contactFormRequestDto) {
    ContactForm response = supportService.createMessage(contactFormRequestDto);
    sendEmail(response.getId());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  private void sendEmail(UUID uuid) {
    applicationEventPublisher.publishEvent(new OnSupportEvent(uuid));
  }
}
