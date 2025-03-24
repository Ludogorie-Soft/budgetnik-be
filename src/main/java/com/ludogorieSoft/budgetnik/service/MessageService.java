package com.ludogorieSoft.budgetnik.service;

import com.ludogorieSoft.budgetnik.dto.request.MessageRequestDto;
import com.ludogorieSoft.budgetnik.dto.request.SystemMessageRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.MessageResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.SystemMessageResponseDto;
import com.ludogorieSoft.budgetnik.model.Message;
import com.ludogorieSoft.budgetnik.model.SystemMessage;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponseDto createPromoMessage(MessageRequestDto requestDto);
    Message findPromoMessageById(UUID id);
    MessageResponseDto getPromoMessage(UUID id);
    List<MessageResponseDto> getAllPromoMessages();
    MessageResponseDto deletePromoMessage(UUID id);

    SystemMessageResponseDto createSystemMessage(SystemMessageRequestDto requestDto);
    SystemMessage findSystemMessageById(UUID id);
    SystemMessageResponseDto getSystemMessage(UUID id);
    List<SystemMessageResponseDto> getAllSystemMessages();
    SystemMessageResponseDto deleteSystemMessage(UUID id);

    List<MessageResponseDto> getAllUserPromoMessages(UUID userId);
    List<SystemMessageResponseDto> getAllUserSystemMessages(UUID userId);
}
