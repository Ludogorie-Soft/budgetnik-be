package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.model.ExpoPushToken;
import com.ludogorieSoft.budgetnik.repository.ExponentPushTokenRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final ExponentPushTokenRepository exponentPushTokenRepository;

  private static final String EXPO_PUSH_API_URL = "https://exp.host/--/api/v2/push/send";
  private static final RestTemplate restTemplate = new RestTemplate();

  public void multipleNotificationSend(String title, String body) {
    getAllExpoPushTokens().forEach(token -> sendPushNotification(token.getToken(), title, body));
  }

  private void sendPushNotification(String expoPushToken, String title, String body) {
    try {
      JSONObject jsonBody = new JSONObject();
      jsonBody.put("to", expoPushToken);
      jsonBody.put("title", title);
      jsonBody.put("body", body);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);

      HttpEntity<String> entity = new HttpEntity<>(jsonBody.toString(), headers);

      restTemplate.exchange(EXPO_PUSH_API_URL, HttpMethod.POST, entity, String.class);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private List<ExpoPushToken> getAllExpoPushTokens() {
    return exponentPushTokenRepository.findAll();
  }
}
