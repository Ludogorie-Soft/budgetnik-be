package com.ludogorieSoft.budgetnik.controller;

import com.ludogorieSoft.budgetnik.dto.request.PaymentRequest;
import com.ludogorieSoft.budgetnik.dto.response.PaymentResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.EphemeralKey;
import com.stripe.model.PaymentIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.EphemeralKeyCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.publishable.key}")
    private String stripePublishableKey;

    @Value("${stripe.currency}")
    private String currency;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPaymentIntent(@RequestBody PaymentRequest paymentRequest) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        // Get the amount from request payload
        double amountValue = Double.parseDouble(paymentRequest.getAmount());
        long amountInCents = (long) (amountValue * 100);

        // Create Customer
        Customer customer = Customer.create(CustomerCreateParams.builder().build());

        // Create Ephemeral Key
        EphemeralKeyCreateParams params = EphemeralKeyCreateParams
                .builder()
                .setCustomer(customer.getId())
                .setStripeVersion("2025-01-27.acacia")
                .build();

        EphemeralKey ephemeralKey = EphemeralKey.create(params);

        // Create PaymentIntent
        PaymentIntent paymentIntent = PaymentIntent.create(
                PaymentIntentCreateParams.builder()
                        .setAmount(amountInCents)
                        .setCurrency(currency)
                        .setCustomer(customer.getId())
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                        .setEnabled(true)
                                        .build()
                        )
                        .build()
        );

        // Build response
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentIntent(paymentIntent.getClientSecret());
        paymentResponse.setEphemeralKey(ephemeralKey.getSecret());
        paymentResponse.setCustomerId(customer.getId());
        paymentResponse.setPublishableKey(stripePublishableKey);

        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }
}




