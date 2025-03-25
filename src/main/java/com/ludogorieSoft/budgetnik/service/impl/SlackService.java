package com.ludogorieSoft.budgetnik.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
public class SlackService {

    @Value("${slack.webhook.url}")
    private String slackWebhookUrl;

    private final RestTemplate restTemplate;

    public void sendMessage(String message) {
        try {
            // Подготвяне на съобщението за Slack
            String payload = "{\"text\": \"" + message + "\"}";

            // Настройка на HTTP заглавия
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Изпращане на HTTP заявка
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);
            restTemplate.postForObject(slackWebhookUrl, entity, String.class);
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
        }
    }
}