package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.request.MessageRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.MessageResponseDto;
import com.ludogorieSoft.budgetnik.exception.MessageNotFoundException;
import com.ludogorieSoft.budgetnik.model.Message;
import com.ludogorieSoft.budgetnik.repository.MessageRepository;
import com.ludogorieSoft.budgetnik.service.MessageService;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final ModelMapper modelMapper;
  private final MessageSource messageSource;

  @Override
  public MessageResponseDto createMessage(MessageRequestDto requestDto) {
    Message message = modelMapper.map(requestDto, Message.class);
    message.setDate(LocalDate.now());
    messageRepository.save(message);
    return modelMapper.map(message, MessageResponseDto.class);
  }

  @Override
  public Message findById(UUID id) {
    return messageRepository
        .findById(id)
        .orElseThrow(() -> new MessageNotFoundException(messageSource));
  }

  @Override
  public MessageResponseDto getMessage(UUID id) {
    Message message = findById(id);
    return modelMapper.map(message, MessageResponseDto.class);
  }

  @Override
  public List<MessageResponseDto> getAllMessages() {
    return messageRepository.findAll().stream()
        .map(m -> modelMapper.map(m, MessageResponseDto.class))
        .toList();
  }

  @Override
  public MessageResponseDto deleteMessage(UUID id) {
    Message message = findById(id);
    messageRepository.delete(message);
    return modelMapper.map(message, MessageResponseDto.class);
  }
}
