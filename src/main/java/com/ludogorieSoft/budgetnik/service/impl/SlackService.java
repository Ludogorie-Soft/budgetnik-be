package com.ludogorieSoft.budgetnik.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class SlackService {

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    private final RestTemplate restTemplate;

    public void sendMessage(String message) {
        try {
            String payload = "{\"text\": \"" + getLocalDateNow() + "---" + message + "\"}";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(payload, headers);
            restTemplate.postForObject(slackWebhookUrl, entity, String.class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private static String getLocalDateNow() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return now.format(formatter);
    }
}