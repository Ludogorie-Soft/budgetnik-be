package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.model.ExpoPushToken;
import com.ludogorieSoft.budgetnik.repository.ExponentPushTokenRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final ExponentPushTokenRepository exponentPushTokenRepository;
  private static final String EXPO_PUSH_API_URL = "https://exp.host/--/api/v2/push/send";
  private static final RestTemplate restTemplate = new RestTemplate();
  private static final int BATCH_SIZE = 100;

  public void multipleNotificationSend(String title, String body) {
    List<ExpoPushToken> tokens = getAllExpoPushTokens();

    for (int i = 0; i < tokens.size(); i += BATCH_SIZE) {
      List<ExpoPushToken> batch = tokens.subList(i, Math.min(i + BATCH_SIZE, tokens.size()));
      sendPushNotifications(batch, title, body);
    }
  }

  private void sendPushNotifications(List<ExpoPushToken> tokens, String title, String body) {
    try {
      JSONArray messages = new JSONArray();

      tokens.forEach(token -> {
        JSONObject message = new JSONObject();
        message.put("to", token.getToken());
        message.put("title", title);
        message.put("body", body);
        messages.put(message);
      });

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<String> entity = new HttpEntity<>(messages.toString(), headers);

      restTemplate.exchange(EXPO_PUSH_API_URL, HttpMethod.POST, entity, String.class);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private List<ExpoPushToken> getAllExpoPushTokens() {
    return exponentPushTokenRepository.findAll();
  }
}
