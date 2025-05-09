package com.ludogorieSoft.budgetnik.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SubscriptionPlanRequest {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private Long amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Interval is required")
    private String interval;

    private String features;

    // JSON translations for name, description, and features
    private String translations;
}
