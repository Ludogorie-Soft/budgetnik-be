package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.request.ContactFormRequestDto;
import com.ludogorieSoft.budgetnik.exception.QueryNotFoundException;
import com.ludogorieSoft.budgetnik.model.ContactForm;
import com.ludogorieSoft.budgetnik.repository.SupportRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportService {

  private final ModelMapper modelMapper;
  private final SupportRepository supportRepository;
  private final MessageSource messageSource;

  public ContactForm createMessage(ContactFormRequestDto request) {
    ContactForm contactForm = modelMapper.map(request, ContactForm.class);
    return supportRepository.save(contactForm);
  }

  public ContactForm findById(UUID id) {
    return supportRepository
        .findById(id)
        .orElseThrow(() -> new QueryNotFoundException(messageSource));
  }
}
