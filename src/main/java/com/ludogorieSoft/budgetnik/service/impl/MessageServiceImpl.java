package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.request.MessageRequestDto;
import com.ludogorieSoft.budgetnik.dto.request.SystemMessageRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.MessageResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.SystemMessageResponseDto;
import com.ludogorieSoft.budgetnik.exception.MessageNotFoundException;
import com.ludogorieSoft.budgetnik.model.Message;
import com.ludogorieSoft.budgetnik.model.SystemMessage;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.repository.PromoMessageRepository;
import com.ludogorieSoft.budgetnik.repository.SystemMessageRepository;
import com.ludogorieSoft.budgetnik.repository.UserRepository;
import com.ludogorieSoft.budgetnik.service.MessageService;
import com.ludogorieSoft.budgetnik.service.UserService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final PromoMessageRepository promoMessageRepository;
  private final SystemMessageRepository systemMessageRepository;
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;
  private final MessageSource messageSource;
  private final NotificationService notificationService;
  private final UserService userService;
  private final EntityManager entityManager;

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
    SystemMessage savedMessage = systemMessageRepository.save(systemMessage);

    notificationService.multipleSystemNotificationSend(savedMessage);

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

  @Override
  public List<MessageResponseDto> getAllUserPromoMessages(UUID userId) {
    User user = userService.findById(userId);
    return user.getPromoMessages().stream()
        .map(m -> modelMapper.map(m, MessageResponseDto.class))
        .toList();
  }

  @Override
  public List<SystemMessageResponseDto> getAllUserSystemMessages(UUID userId) {
    User user = userService.findById(userId);
    return user.getSystemMessages().stream()
        .map(m -> modelMapper.map(m, SystemMessageResponseDto.class))
        .toList();
  }

  @Override
  public UUID removePromoMessageFromUser(UUID userId, UUID messageId) {
    User user = userService.findById(userId);
    Message message = promoMessageRepository.findById(messageId).orElse(null);

    if (message != null) {
      user.getPromoMessages().remove(message);
    } else {
      user.setPromoMessages(new ArrayList<>(user.getPromoMessages().stream()
              .filter(msg -> !msg.getId().equals(messageId))
              .toList()));
    }

    userRepository.saveAndFlush(user);
    return messageId;
  }

  @Override
  public UUID removeSystemMessageFromUser(UUID userId, UUID messageId) {
    User user = userService.findById(userId);
    SystemMessage message = systemMessageRepository.findById(messageId).orElse(null);

    if (message != null) {
      user.getSystemMessages().remove(message);
    } else {
      user.setSystemMessages(new ArrayList<>(user.getSystemMessages().stream()
              .filter(msg -> !msg.getId().equals(messageId))
              .toList()));
    }

    userRepository.saveAndFlush(user);
    entityManager.clear();
    return messageId;
  }
}
