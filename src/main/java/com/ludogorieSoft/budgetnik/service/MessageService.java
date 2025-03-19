package com.ludogorieSoft.budgetnik.service;

import com.ludogorieSoft.budgetnik.dto.request.MessageRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.MessageResponseDto;
import com.ludogorieSoft.budgetnik.model.Message;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto createMessage(MessageRequestDto requestDto);
    Message findById(UUID id);
    MessageResponseDto getMessage(UUID id);
    List<MessageResponseDto> getAllMessages();
    MessageResponseDto deleteMessage(UUID id);
}
