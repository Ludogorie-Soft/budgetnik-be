package com.ludogorieSoft.budgetnik.service;

import com.ludogorieSoft.budgetnik.dto.response.SubscriptionResponse;
import com.ludogorieSoft.budgetnik.model.Subscription;
import com.ludogorieSoft.budgetnik.model.User;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {
    /**
     * Find subscription by customer ID
     * @param customerId Stripe customer ID
     * @return Subscription
     */
    Subscription findByCustomerId(String customerId);

    /**
     * Get all subscriptions
     * @return List of SubscriptionResponse
     */
    List<SubscriptionResponse> getAllSubscriptions();

    /**
     * Create a subscription for a user
     * @param userId User ID
     * @param paymentMethodId Stripe payment method ID
     * @param priceId Stripe price ID
     * @return SubscriptionResponse
     */
    SubscriptionResponse createSubscription(UUID userId, String paymentMethodId, String priceId);

    /**
     * Get current subscription for a user
     * @param userId User ID
     * @return SubscriptionResponse
     */
    SubscriptionResponse getCurrentSubscription(UUID userId);

    /**
     * Cancel a subscription
     * @param userId User ID
     * @return SubscriptionResponse
     */
    SubscriptionResponse cancelSubscription(UUID userId);

    /**
     * Check if a user has an active subscription
     * @param user User
     * @return boolean
     */
    boolean hasActiveSubscription(User user);

    /**
     * Calculate remaining trial days for a user
     * @param user User
     * @return int
     */
    int calculateRemainingTrialDays(User user);
}
