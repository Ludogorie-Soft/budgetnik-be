package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.request.MessageRequestDto;
import com.ludogorieSoft.budgetnik.dto.request.SystemMessageRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.MessageResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.SystemMessageResponseDto;
import com.ludogorieSoft.budgetnik.exception.MessageNotFoundException;
import com.ludogorieSoft.budgetnik.model.Message;
import com.ludogorieSoft.budgetnik.model.SystemMessage;
import com.ludogorieSoft.budgetnik.repository.PromoMessageRepository;
import com.ludogorieSoft.budgetnik.repository.SystemMessageRepository;
import com.ludogorieSoft.budgetnik.service.MessageService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final PromoMessageRepository promoMessageRepository;
  private final SystemMessageRepository systemMessageRepository;
  private final ModelMapper modelMapper;
  private final MessageSource messageSource;
  private final NotificationService notificationService;

  @Override
  public MessageResponseDto createPromoMessage(MessageRequestDto requestDto) {
    Message message = modelMapper.map(requestDto, Message.class);
    message.setDate(LocalDate.now());
    Message savedMessage = promoMessageRepository.save(message);

    notificationService.multiplePromoNotificationSend(savedMessage);

    return modelMapper.map(message, MessageResponseDto.class);
  }

  @Override
  public Message findPromoMessageById(UUID id) {
    return promoMessageRepository
        .findById(id)
        .orElseThrow(() -> new MessageNotFoundException(messageSource));
  }

  @Override
  public MessageResponseDto getPromoMessage(UUID id) {
    Message message = findPromoMessageById(id);
    return modelMapper.map(message, MessageResponseDto.class);
  }

  @Override
  public List<MessageResponseDto> getAllPromoMessages() {
    return promoMessageRepository.findAll().stream()
        .map(m -> modelMapper.map(m, MessageResponseDto.class))
        .toList();
  }

  @Override
  public MessageResponseDto deletePromoMessage(UUID id) {
    Message message = findPromoMessageById(id);
    promoMessageRepository.delete(message);
    return modelMapper.map(message, MessageResponseDto.class);
  }

  @Override
  public SystemMessageResponseDto createSystemMessage(SystemMessageRequestDto requestDto) {
    SystemMessage systemMessage = modelMapper.map(requestDto, SystemMessage.class);
    systemMessage.setDate(LocalDate.now());
    systemMessageRepository.save(systemMessage);

    notificationService.multipleSystemNotificationSend(requestDto.getTitle(), requestDto.getBody());

    return modelMapper.map(systemMessage, SystemMessageResponseDto.class);
  }

  @Override
  public SystemMessage findSystemMessageById(UUID id) {
    return systemMessageRepository
        .findById(id)
        .orElseThrow(() -> new MessageNotFoundException(messageSource));
  }

  @Override
  public SystemMessageResponseDto getSystemMessage(UUID id) {
    SystemMessage systemMessage = findSystemMessageById(id);
    return modelMapper.map(systemMessage, SystemMessageResponseDto.class);
  }

  @Override
  public List<SystemMessageResponseDto> getAllSystemMessages() {
    return systemMessageRepository.findAll().stream()
        .map(m -> modelMapper.map(m, SystemMessageResponseDto.class))
        .toList();
  }

  @Override
  public SystemMessageResponseDto deleteSystemMessage(UUID id) {
    SystemMessage systemMessage = findSystemMessageById(id);
    systemMessageRepository.delete(systemMessage);
    return modelMapper.map(systemMessage, SystemMessageResponseDto.class);
  }
}
