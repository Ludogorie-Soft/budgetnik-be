package com.ludogorieSoft.budgetnik.controller;

import com.ludogorieSoft.budgetnik.dto.request.PushTokenRequest;
import com.ludogorieSoft.budgetnik.dto.response.MessageResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.SystemMessageResponseDto;
import com.ludogorieSoft.budgetnik.service.MessageService;
import com.ludogorieSoft.budgetnik.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final MessageService messageService;

  @PutMapping("/exponent-push-token")
  public ResponseEntity<String> updatePushToken(
      @RequestParam("id") UUID id, @RequestBody PushTokenRequest pushTokenRequest) {
    userService.updateExponentPushToken(id, pushTokenRequest.getToken());
    return ResponseEntity.ok().body("Token updated successfully!");
  }

  @DeleteMapping("/exponent-push-token")
  public ResponseEntity<String> deletePushToken(@RequestBody PushTokenRequest pushTokenRequest) {
    userService.deleteExponentPushToken(pushTokenRequest.getToken());
    return ResponseEntity.ok().body("Push token deleted!");
  }

  @GetMapping("/messages")
  public ResponseEntity<List<MessageResponseDto>> getAllUserMessages(@RequestParam("id") UUID id) {
    List<MessageResponseDto> response = messageService.getAllUserPromoMessages(id);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @DeleteMapping("/messages")
  public ResponseEntity<UUID> removePromoMessageFromUser(
      @RequestParam("userId") UUID userId, @RequestParam("messageId") UUID messageId) {
    UUID response = messageService.removePromoMessageFromUser(userId, messageId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/messages/system")
  public ResponseEntity<List<SystemMessageResponseDto>> getAllUserSystemMessages(
      @RequestParam("id") UUID id) {
    List<SystemMessageResponseDto> response = messageService.getAllUserSystemMessages(id);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @DeleteMapping("/messages/system")
  public ResponseEntity<UUID> removeSystemMessageFromUser(
      @RequestParam("userId") UUID userId, @RequestParam("messageId") UUID messageId) {
    UUID response = messageService.removeSystemMessageFromUser(userId, messageId);
    System.out.println("deleted");
    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
