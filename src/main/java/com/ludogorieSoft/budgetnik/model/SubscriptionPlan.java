package com.ludogorieSoft.budgetnik.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscription_plans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String description;

    // Price in the smallest currency unit (e.g., cents)
    private Long amount;

    // Currency code (e.g., bgn)
    private String currency;

    // Billing interval: month or year
    private String interval;

    // Comma-separated list of features
    private String features;

    // Stripe product ID
    private String stripeProductId;

    // Stripe price ID
    private String stripePriceId;

    // Whether this plan is active
    private boolean active;

    // JSON translations for name, description, and features
    @Column(columnDefinition = "jsonb")
    private String translations;
}
