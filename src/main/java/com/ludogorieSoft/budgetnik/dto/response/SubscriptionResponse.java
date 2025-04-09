package com.ludogorieSoft.budgetnik.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SubscriptionResponse {
    private UUID id;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String paymentIntentId;
    private String customerId;
    private Long amount;
}
