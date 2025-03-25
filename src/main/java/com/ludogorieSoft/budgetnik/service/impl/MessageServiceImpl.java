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
import jakarta.transaction.Transactional;
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
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;
  private final MessageSource messageSource;
  private final NotificationService notificationService;
  private final UserService userService;

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
    userRepository.removePromoMessageFromAllUsers(id);
    promoMessageRepository.deleteById(id);
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
  @Transactional
  public SystemMessageResponseDto deleteSystemMessage(UUID id) {
    SystemMessage systemMessage = findSystemMessageById(id);
    userRepository.removeMessageFromAllUsers(id);
    systemMessageRepository.deleteById(id);
    return modelMapper.map(systemMessage, SystemMessageResponseDto.class);
  }

  @Override
  public List<MessageResponseDto> getAllUserPromoMessages(UUID userId) {
    User user = userService.findById(userId);

    List<Message> promoMessages =
        promoMessageRepository.findByIdIn(
            user.getPromoMessages().stream().map(Message::getId).toList());

    return promoMessages.stream().map(m -> modelMapper.map(m, MessageResponseDto.class)).toList();
  }

  @Override
  public List<SystemMessageResponseDto> getAllUserSystemMessages(UUID userId) {
    User user = userService.findById(userId);

    List<SystemMessage> systemMessages =
        systemMessageRepository.findByIdIn(
            user.getSystemMessages().stream().map(SystemMessage::getId).toList());

    return systemMessages.stream()
        .map(m -> modelMapper.map(m, SystemMessageResponseDto.class))
        .toList();
  }

  @Override
  public List<UUID> removePromoMessageFromUser(UUID userId, List<UUID> messageIds) {
    User user = userService.findById(userId);
    user.getPromoMessages().removeIf(msg -> messageIds.contains(msg.getId()));
    userRepository.save(user);
    return messageIds;
  }

  @Override
  public List<UUID> removeSystemMessageFromUser(UUID userId, List<UUID> messageIds) {
    User user = userService.findById(userId);
    user.getSystemMessages().removeIf(msg -> messageIds.contains(msg.getId()));
    userRepository.save(user);
    return messageIds;
  }
}
