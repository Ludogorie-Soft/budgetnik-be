package com.ludogorieSoft.budgetnik.controller;

import com.ludogorieSoft.budgetnik.dto.request.MessageRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.MessageResponseDto;
import com.ludogorieSoft.budgetnik.service.MessageService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/messages")
@RequiredArgsConstructor
public class MessageController {

  private final MessageService messageService;

  @PostMapping
  public ResponseEntity<MessageResponseDto> createMessage(
      @RequestBody MessageRequestDto requestDto) {
    MessageResponseDto response = messageService.createMessage(requestDto);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<MessageResponseDto>> getAllMessages() {
    List<MessageResponseDto> response = messageService.getAllMessages();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/message")
  public ResponseEntity<MessageResponseDto> getMessage(@RequestParam("id") UUID id) {
    MessageResponseDto response = messageService.getMessage(id);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @DeleteMapping
  public ResponseEntity<MessageResponseDto> deleteMessage(@RequestParam("id") UUID id) {
    MessageResponseDto response = messageService.deleteMessage(id);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
