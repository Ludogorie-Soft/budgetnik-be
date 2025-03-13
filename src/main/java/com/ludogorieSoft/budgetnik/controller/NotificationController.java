package com.ludogorieSoft.budgetnik.controller;

import com.ludogorieSoft.budgetnik.dto.request.NotificationRequest;
import com.ludogorieSoft.budgetnik.service.impl.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/notifications")
@RestController
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @PostMapping("/send")
  public ResponseEntity<String> sendNotification(
      @RequestBody NotificationRequest notificationRequest) {
    notificationService.multipleNotificationSend(
        notificationRequest.getTitle(), notificationRequest.getBody());
    return ResponseEntity.ok("Notification sent successfully!");
  }
}
