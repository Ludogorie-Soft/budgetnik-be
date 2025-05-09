package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.response.SubscriptionResponse;
import com.ludogorieSoft.budgetnik.model.Subscription;
import com.ludogorieSoft.budgetnik.model.SubscriptionPlan;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.repository.SubscriptionRepository;
import com.ludogorieSoft.budgetnik.service.SubscriptionPlanService;
import com.ludogorieSoft.budgetnik.service.SubscriptionService;
import com.ludogorieSoft.budgetnik.service.StripeService;
import com.ludogorieSoft.budgetnik.service.UserService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {

  private final SubscriptionRepository subscriptionRepository;
  private final UserService userService;
  private final SubscriptionPlanService subscriptionPlanService;
  private final StripeService stripeService;
  private final ModelMapper modelMapper;

  @Override
  public Subscription findByCustomerId(String customerId) {
    return subscriptionRepository.findByCustomerId(customerId).orElse(null);
  }

  @Override
  public List<SubscriptionResponse> getAllSubscriptions() {
    return subscriptionRepository.findAll().stream()
        .map(s -> modelMapper.map(s, SubscriptionResponse.class))
        .toList();
  }

  @Override
  @Transactional
  public SubscriptionResponse createSubscription(UUID userId, String paymentMethodId, String priceId) {
    try {
      // Get user
      User user = userService.findById(userId);

      // Get subscription plan
      SubscriptionPlan plan = subscriptionPlanService.findByStripePriceId(priceId);

      // Calculate trial period
      int trialDays = calculateRemainingTrialDays(user);

      // Create Stripe subscription
      com.stripe.model.Subscription stripeSubscription = stripeService.createSubscription(
              user.getCustomerId(),
              priceId,
              paymentMethodId,
              trialDays
      );

      // Create subscription in database
      Subscription subscription = new Subscription();
      subscription.setStartDate(LocalDateTime.now());
      subscription.setEndDate(LocalDateTime.now().plusMonths("month".equals(plan.getInterval()) ? 1 : 12));
      subscription.setCustomerId(user.getCustomerId());
      subscription.setAmount(plan.getAmount());
      subscription.setStripeSubscriptionId(stripeSubscription.getId());
      subscription.setStatus(stripeSubscription.getStatus());
      subscription.setSubscriptionPlan(plan);

      // Save subscription
      Subscription savedSubscription = subscriptionRepository.save(subscription);

      // Update user
      user.setSubscription(savedSubscription);
      userService.saveUser(user);

      return modelMapper.map(savedSubscription, SubscriptionResponse.class);
    } catch (StripeException e) {
      log.error("Error creating subscription in Stripe", e);
      throw new RuntimeException("Error creating subscription: " + e.getMessage());
    }
  }

  @Override
  public SubscriptionResponse getCurrentSubscription(UUID userId) {
    User user = userService.findById(userId);
    Subscription subscription = user.getSubscription();

    if (subscription == null) {
      return null;
    }

    return modelMapper.map(subscription, SubscriptionResponse.class);
  }

  @Override
  @Transactional
  public SubscriptionResponse cancelSubscription(UUID userId) {
    try {
      User user = userService.findById(userId);
      Subscription subscription = user.getSubscription();

      if (subscription == null || subscription.getStripeSubscriptionId() == null) {
        throw new RuntimeException("No active subscription found for user");
      }

      // Cancel in Stripe
      com.stripe.model.Subscription canceledSubscription = stripeService.cancelSubscription(
              subscription.getStripeSubscriptionId()
      );

      // Update subscription status
      subscription.setStatus(canceledSubscription.getStatus());
      Subscription updatedSubscription = subscriptionRepository.save(subscription);

      return modelMapper.map(updatedSubscription, SubscriptionResponse.class);
    } catch (StripeException e) {
      log.error("Error canceling subscription in Stripe", e);
      throw new RuntimeException("Error canceling subscription: " + e.getMessage());
    }
  }

  @Override
  public boolean hasActiveSubscription(User user) {
    Subscription subscription = user.getSubscription();

    if (subscription == null) {
      return false;
    }

    // Check if subscription is active
    return "active".equals(subscription.getStatus()) ||
           "trialing".equals(subscription.getStatus()) ||
           subscription.getEndDate().isAfter(LocalDateTime.now());
  }

  @Override
  public int calculateRemainingTrialDays(User user) {
    // If user already has a subscription, no trial
    if (user.getSubscription() != null) {
      return 0;
    }

    // Calculate days since registration
    LocalDateTime createdAt = user.getCreatedAt();
    LocalDateTime now = LocalDateTime.now();
    long daysSinceRegistration = ChronoUnit.DAYS.between(createdAt, now);

    // Free trial is 30 days
    int trialDays = 30 - (int) daysSinceRegistration;

    // If trial period has expired, no trial
    return Math.max(0, trialDays);
  }
}
