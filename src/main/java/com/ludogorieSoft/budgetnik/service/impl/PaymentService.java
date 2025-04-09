package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.model.Subscription;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.repository.SubscriptionRepository;
import com.ludogorieSoft.budgetnik.repository.UserRepository;
import com.ludogorieSoft.budgetnik.service.UserService;
import com.stripe.model.PaymentIntent;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
  private final UserService userService;
  private final SubscriptionRepository subscriptionRepository;
  private final UserRepository userRepository;

  private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

  @Transactional
  public void createSubscription(PaymentIntent paymentIntent, String email) {
    User user = userService.findByEmail(email);

    Subscription subscription = createSubscriptionInDb(paymentIntent);

    user.setSubscription(subscription);
    userRepository.save(user);

    logger.info("Created subscription with ID " + subscription.getId() + " for user with id " + user.getId());
  }

  @NotNull
  private Subscription createSubscriptionInDb(PaymentIntent paymentIntent) {
    Subscription subscription = new Subscription();
    subscription.setStartDate(LocalDateTime.now());
    subscription.setEndDate(LocalDateTime.now().plusMonths(1));
    subscription.setPaymentIntentId(paymentIntent.getId());
    subscription.setCustomerId(paymentIntent.getCustomer());
    subscription.setAmount(paymentIntent.getAmount());
    return subscriptionRepository.save(subscription);
  }
}
