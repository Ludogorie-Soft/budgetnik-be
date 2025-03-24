package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.event.OnSendMessageEvent;
import com.ludogorieSoft.budgetnik.model.ExpoPushToken;
import com.ludogorieSoft.budgetnik.model.Message;
import com.ludogorieSoft.budgetnik.model.SystemMessage;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.repository.ExponentPushTokenRepository;
import com.ludogorieSoft.budgetnik.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

  private final ExponentPushTokenRepository exponentPushTokenRepository;
  private final ApplicationEventPublisher applicationEventPublisher;
  private final UserRepository userRepository;
  private static final String EXPO_PUSH_API_URL = "https://exp.host/--/api/v2/push/send";
  private static final RestTemplate restTemplate = new RestTemplate();
  private static final int BATCH_SIZE = 100;

  public void multiplePromoNotificationSend(Message message) {
    List<ExpoPushToken> tokens = getAllExpoPushTokens();

    for (int i = 0; i < tokens.size(); i += BATCH_SIZE) {
      List<ExpoPushToken> batch = tokens.subList(i, Math.min(i + BATCH_SIZE, tokens.size()));
      sendPushPromoNotifications(batch, message);
    }
  }

  public void multipleSystemNotificationSend(SystemMessage systemMessage) {
    List<ExpoPushToken> tokens = getAllExpoPushTokens();

    for (int i = 0; i < tokens.size(); i += BATCH_SIZE) {
      List<ExpoPushToken> batch = tokens.subList(i, Math.min(i + BATCH_SIZE, tokens.size()));
      sendPushSystemNotifications(batch, systemMessage);
    }
  }

  private void sendPushSystemNotifications(List<ExpoPushToken> tokens, SystemMessage systemMessage) {
    try {
      JSONArray messages = new JSONArray();

      tokens.forEach(token -> {

        setSystemMessagesToUser(systemMessage, token);

        JSONObject message = new JSONObject();
        message.put("to", token.getToken());
        message.put("title", systemMessage.getTitle());
        message.put("body", systemMessage.getBody());
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

  private void setSystemMessagesToUser(SystemMessage systemMessage, ExpoPushToken token) {
    User user = token.getUser();
    List<SystemMessage> systemMessages = user.getSystemMessages();
    systemMessages.add(systemMessage);
    user.setSystemMessages(systemMessages);
    userRepository.save(user);
  }

  private void sendPushPromoNotifications(List<ExpoPushToken> tokens, Message promo) {

    try {
      JSONArray messages = new JSONArray();

      tokens.forEach(token -> {
        applicationEventPublisher.publishEvent(new OnSendMessageEvent(token.getUser().getEmail(), promo.getId()));
        setPromoMessageToUser(promo, token);

        JSONObject message = new JSONObject();
        message.put("to", token.getToken());
        message.put("title", promo.getTitle());
        message.put("body", promo.getBody());
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

  private void setPromoMessageToUser(Message promo, ExpoPushToken token) {
    User user = token.getUser();
    List<Message> promoMessages = user.getPromoMessages();
    promoMessages.add(promo);
    user.setPromoMessages(promoMessages);
    userRepository.save(user);
  }

  private List<ExpoPushToken> getAllExpoPushTokens() {
    return exponentPushTokenRepository.findAll();
  }
}
