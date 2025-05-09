package com.ludogorieSoft.budgetnik.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class SubscriptionPlanResponse {
    private UUID id;
    private String name;
    private String description;
    private Long amount;
    private String currency;
    private String interval;
    private String features;
    private String stripePriceId;
    private String translations;
}
