package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.service.StripeService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.Subscription;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.SubscriptionUpdateParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Override
    public Customer createCustomer(String email, String name) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(email)
                .setName(name)
                .build();
        
        return Customer.create(params);
    }

    @Override
    public Product createProduct(String name, String description) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        
        ProductCreateParams params = ProductCreateParams.builder()
                .setName(name)
                .setDescription(description)
                .setActive(true)
                .build();
        
        return Product.create(params);
    }

    @Override
    public Price createPrice(String productId, Long amount, String currency, String interval) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        
        PriceCreateParams.Recurring recurring;
        if ("month".equals(interval)) {
            recurring = PriceCreateParams.Recurring.builder()
                    .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                    .build();
        } else if ("year".equals(interval)) {
            recurring = PriceCreateParams.Recurring.builder()
                    .setInterval(PriceCreateParams.Recurring.Interval.YEAR)
                    .build();
        } else {
            throw new IllegalArgumentException("Interval must be 'month' or 'year'");
        }
        
        PriceCreateParams params = PriceCreateParams.builder()
                .setProduct(productId)
                .setUnitAmount(amount)
                .setCurrency(currency)
                .setRecurring(recurring)
                .build();
        
        return Price.create(params);
    }

    @Override
    public Subscription createSubscription(String customerId, String priceId, String paymentMethodId, Integer trialPeriodDays) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        
        // Attach payment method to customer
        attachPaymentMethod(paymentMethodId, customerId);
        
        // Set as default payment method
        setDefaultPaymentMethod(customerId, paymentMethodId);
        
        SubscriptionCreateParams.Item item = SubscriptionCreateParams.Item.builder()
                .setPrice(priceId)
                .build();
        
        SubscriptionCreateParams.Builder paramsBuilder = SubscriptionCreateParams.builder()
                .setCustomer(customerId)
                .addItem(item)
                .setDefaultPaymentMethod(paymentMethodId);
        
        // Add trial period if specified
        if (trialPeriodDays != null && trialPeriodDays > 0) {
            paramsBuilder.setTrialPeriodDays(Long.valueOf(trialPeriodDays));
        }
        
        return Subscription.create(paramsBuilder.build());
    }

    @Override
    public PaymentMethod attachPaymentMethod(String paymentMethodId, String customerId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        
        PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
        PaymentMethodAttachParams params = PaymentMethodAttachParams.builder()
                .setCustomer(customerId)
                .build();
        
        return paymentMethod.attach(params);
    }

    @Override
    public void setDefaultPaymentMethod(String customerId, String paymentMethodId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        
        Customer customer = Customer.retrieve(customerId);
        Map<String, Object> params = new HashMap<>();
        params.put("invoice_settings", Map.of("default_payment_method", paymentMethodId));
        
        customer.update(params);
    }

    @Override
    public Subscription cancelSubscription(String subscriptionId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        
        Subscription subscription = Subscription.retrieve(subscriptionId);
        SubscriptionUpdateParams params = SubscriptionUpdateParams.builder()
                .setCancelAtPeriodEnd(true)
                .build();
        
        return subscription.update(params);
    }

    @Override
    public Subscription retrieveSubscription(String subscriptionId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;
        
        return Subscription.retrieve(subscriptionId);
    }
}
