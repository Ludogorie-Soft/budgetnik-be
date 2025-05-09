package com.ludogorieSoft.budgetnik.controller;

import com.ludogorieSoft.budgetnik.dto.request.PushTokenRequest;
import com.ludogorieSoft.budgetnik.dto.request.RemoveMessageRequestDto;
import com.ludogorieSoft.budgetnik.dto.response.MessageResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.SubscriptionResponse;
import com.ludogorieSoft.budgetnik.dto.response.SystemMessageResponseDto;
import com.ludogorieSoft.budgetnik.dto.response.UserResponse;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.service.MessageService;
import com.ludogorieSoft.budgetnik.service.SubscriptionService;
import com.ludogorieSoft.budgetnik.service.UserService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
  private final SubscriptionService subscriptionService;

  @PutMapping("/exponent-push-token")
  public ResponseEntity<String> updatePushToken(
      @RequestParam("id") UUID id, @RequestBody PushTokenRequest pushTokenRequest) {
    userService.saveExponentPushToken(id, pushTokenRequest.getToken());
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

  @DeleteMapping("/messages/remove")
  public ResponseEntity<List<UUID>> removePromoMessageFromUser(
      @RequestParam("userId") UUID userId, @RequestBody RemoveMessageRequestDto requestDto) {
    List<UUID> response =
        messageService.removePromoMessageFromUser(userId, requestDto.getMessageIds());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/messages/system")
  public ResponseEntity<List<SystemMessageResponseDto>> getAllUserSystemMessages(
      @RequestParam("id") UUID id) {
    List<SystemMessageResponseDto> response = messageService.getAllUserSystemMessages(id);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @DeleteMapping("/messages/system/remove")
  public ResponseEntity<List<UUID>> removeSystemMessageFromUser(
      @RequestParam("userId") UUID userId, @RequestBody RemoveMessageRequestDto requestDto) {
    List<UUID> response =
        messageService.removeSystemMessageFromUser(userId, requestDto.getMessageIds());
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PutMapping("/rate")
  public ResponseEntity<UserResponse> rateTheApp(
      @RequestParam("id") UUID id, @RequestParam("rating") String rate) {
    UserResponse response = userService.rateTheApp(id, rate);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @PostMapping("/subscriptions")
  public ResponseEntity<SubscriptionResponse> createSubscription(
      @RequestParam("userId") UUID userId,
      @RequestParam("paymentMethodId") String paymentMethodId,
      @RequestParam("priceId") String priceId) {
    SubscriptionResponse response = subscriptionService.createSubscription(userId, paymentMethodId, priceId);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @GetMapping("/subscriptions/current")
  public ResponseEntity<SubscriptionResponse> getCurrentSubscription(@RequestParam("userId") UUID userId) {
    SubscriptionResponse response = subscriptionService.getCurrentSubscription(userId);
    if (response == null) {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @DeleteMapping("/subscriptions")
  public ResponseEntity<SubscriptionResponse> cancelSubscription(@RequestParam("userId") UUID userId) {
    SubscriptionResponse response = subscriptionService.cancelSubscription(userId);
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/subscriptions/trial-days")
  public ResponseEntity<Integer> getRemainingTrialDays(@RequestParam("userId") UUID userId) {
    User user = userService.findById(userId);
    int trialDays = subscriptionService.calculateRemainingTrialDays(user);
    return new ResponseEntity<>(trialDays, HttpStatus.OK);
  }
}
