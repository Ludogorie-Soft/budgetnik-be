package com.ludogorieSoft.budgetnik.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ludogorieSoft.budgetnik.dto.request.PaymentRequest;
import com.ludogorieSoft.budgetnik.dto.response.PaymentResponse;
import com.ludogorieSoft.budgetnik.dto.response.SubscriptionResponse;
import com.ludogorieSoft.budgetnik.event.OnPaymentEvent;
import com.ludogorieSoft.budgetnik.model.ExpoPushToken;
import com.ludogorieSoft.budgetnik.model.SystemMessage;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.repository.ExponentPushTokenRepository;
import com.ludogorieSoft.budgetnik.repository.SystemMessageRepository;
import com.ludogorieSoft.budgetnik.service.SubscriptionService;
import com.ludogorieSoft.budgetnik.service.UserService;
import com.ludogorieSoft.budgetnik.service.impl.NotificationService;
import com.ludogorieSoft.budgetnik.service.impl.PaymentService;
import com.ludogorieSoft.budgetnik.service.impl.SlackService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.EphemeralKey;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.EphemeralKeyCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

  private final PaymentService paymentService;
  private final UserService userService;
  private final SlackService slackService;
  private final NotificationService notificationService;
  private final ExponentPushTokenRepository exponentPushTokenRepository;
  private final SystemMessageRepository systemMessageRepository;
  private final SubscriptionService subscriptionService;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Value("${stripe.secret.key}")
  private String stripeSecretKey;

  @Value("${stripe.publishable.key}")
  private String stripePublishableKey;

  @Value("${stripe.currency}")
  private String currency;

  @Value("${stripe.webhook.secret}")
  private String endpointSecret;

  @PostMapping("/webhook")
  public ResponseEntity<String> handleStripeEvent(
      @RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader)
      throws JsonProcessingException {

    Event event;

    try {
      event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
    } catch (SignatureVerificationException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("❌ Signature verification failed");
    }

    if ("payment_intent.succeeded".equals(event.getType())) {
      JsonNode rootNode = new ObjectMapper().readTree(payload);
      JsonNode paymentIntentNode = rootNode.path("data").path("object");

      PaymentIntent paymentIntent =
          ApiResource.GSON.fromJson(paymentIntentNode.toString(), PaymentIntent.class);
      if (paymentIntent == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid PaymentIntent data");
      }

      String customerId = paymentIntent.getCustomer();

      if (customerId == null || customerId.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PaymentIntent has no customer");
      }

      User user = userService.findByCustomerId(customerId);
      String email = user.getEmail();

      if (email == null || email.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No email in the PaymentIntent");
      }

      sendNotification(user);
      sendEmail(email);
      paymentService.createSubscription(paymentIntent, email);

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("❌ Failed to parse PaymentIntent");

    } else if ("payment_intent.payment_failed".equals(event.getType())) {

      JsonNode rootNode = new ObjectMapper().readTree(payload);
      JsonNode paymentIntentNode = rootNode.path("data").path("object");

      PaymentIntent paymentIntent =
          ApiResource.GSON.fromJson(paymentIntentNode.toString(), PaymentIntent.class);
      if (paymentIntent == null) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid PaymentIntent data");
      }

      String customerId = paymentIntent.getCustomer();

      if (customerId == null || customerId.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("PaymentIntent has no customer");
      }

      User user = userService.findByCustomerId(customerId);
      String email = user.getEmail();

      if (email == null || email.isEmpty()) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No email in the PaymentIntent");
      }

      String failureMessage =
          paymentIntent.getLastPaymentError() != null
              ? paymentIntent.getLastPaymentError().getMessage()
              : "Unknown error";

      slackService.sendMessage(
          "Payment failed for user with id: " + user.getId() + " — Reason: " + failureMessage);

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("❌ Failed to handle payment failure");
    }

    return ResponseEntity.ok("Webhook processed successfully");
  }

  @PostMapping
  public ResponseEntity<PaymentResponse> createPaymentIntent(
      @RequestBody PaymentRequest paymentRequest) throws StripeException {
    Stripe.apiKey = stripeSecretKey;

    double amountValue = Double.parseDouble(paymentRequest.getAmount());
    long amountInCents = (long) (amountValue * 100);

    Customer customer = Customer.create(CustomerCreateParams.builder().build());

    EphemeralKeyCreateParams params =
        EphemeralKeyCreateParams.builder()
            .setCustomer(customer.getId())
            .setStripeVersion("2025-01-27.acacia")
            .build();

    EphemeralKey ephemeralKey = EphemeralKey.create(params);

    PaymentIntent paymentIntent =
        PaymentIntent.create(
            PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency)
                .setCustomer(customer.getId())
                .setAutomaticPaymentMethods(
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                        .setEnabled(true)
                        .build())
                .build());

    PaymentResponse paymentResponse = new PaymentResponse();
    paymentResponse.setPaymentIntent(paymentIntent.getClientSecret());
    paymentResponse.setEphemeralKey(ephemeralKey.getSecret());
    paymentResponse.setCustomerId(customer.getId());
    paymentResponse.setPublishableKey(stripePublishableKey);

    User user = userService.getCurrentUser();
    user.setCustomerId(customer.getId());
    userService.saveUser(user);

    return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
  }

  @GetMapping("/subscriptions")
  public ResponseEntity<List<SubscriptionResponse>> getAllSubscriptions() {
    List<SubscriptionResponse> response = subscriptionService.getAllSubscriptions();
    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  private void sendNotification(User user) {
    List<ExpoPushToken> expoPushTokens = exponentPushTokenRepository.findByUser(user);
    if (!expoPushTokens.isEmpty()) {
      SystemMessage systemMessage = new SystemMessage();
      systemMessage.setDate(LocalDate.now());
      systemMessage.setBody("Успешно направихте абонамент за BUDGETникът.");
      systemMessage.setTitle("BUDGETникът");
      systemMessageRepository.save(systemMessage);
      notificationService.sendPushSystemNotifications(expoPushTokens, systemMessage);
    }
  }

  private void sendEmail(String email) {
    applicationEventPublisher.publishEvent(new OnPaymentEvent(email));
  }
}
