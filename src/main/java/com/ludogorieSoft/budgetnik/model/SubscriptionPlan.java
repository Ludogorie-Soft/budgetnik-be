package com.ludogorieSoft.budgetnik.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

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
    @JdbcTypeCode(SqlTypes.JSON)
    private String translations;
}
