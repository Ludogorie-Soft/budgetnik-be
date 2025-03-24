package com.ludogorieSoft.budgetnik.controller;

import com.ludogorieSoft.budgetnik.dto.request.MessageRequestDto;
import com.ludogorieSoft.budgetnik.dto.request.SystemMessageRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.MessageResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.SystemMessageResponseDto;
import com.ludogorieSoft.budgetnik.service.MessageService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @PostMapping("/admin/messages")
  public ResponseEntity<MessageResponseDto> createMessage(
      @RequestBody MessageRequestDto requestDto) {
    MessageResponseDto response = messageService.createPromoMessage(requestDto);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping("/admin/messages")
  public ResponseEntity<List<MessageResponseDto>> getAllMessages() {
    List<MessageResponseDto> response = messageService.getAllPromoMessages();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/messages/message")
  public ResponseEntity<MessageResponseDto> getMessage(@RequestParam("id") UUID id) {
    MessageResponseDto response = messageService.getPromoMessage(id);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @DeleteMapping("/admin/messages")
  public ResponseEntity<MessageResponseDto> deleteMessage(@RequestParam("id") UUID id) {
    MessageResponseDto response = messageService.deletePromoMessage(id);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/admin/messages/system")
  public ResponseEntity<SystemMessageResponseDto> createSystemMessage(
      @RequestBody SystemMessageRequestDto requestDto) {
    SystemMessageResponseDto responseDto = messageService.createSystemMessage(requestDto);
    return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
  }

  @GetMapping("/admin/messages/system")
  public ResponseEntity<List<SystemMessageResponseDto>> getAllSystemMessages() {
    List<SystemMessageResponseDto> response = messageService.getAllSystemMessages();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/messages/message/system")
  public ResponseEntity<SystemMessageResponseDto> getSystemMessage(@RequestParam("id") UUID id) {
    SystemMessageResponseDto response = messageService.getSystemMessage(id);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @DeleteMapping("/admin/messages/system")
  public ResponseEntity<SystemMessageResponseDto> deleteSystemMessage(@RequestParam("id") UUID id) {
    SystemMessageResponseDto response = messageService.deleteSystemMessage(id);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
