package com.ludogorieSoft.budgetnik.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Price;
import com.stripe.model.Product;
import com.stripe.model.Subscription;

public interface StripeService {
    /**
     * Create a Stripe customer
     * @param email Customer email
     * @param name Customer name
     * @return Stripe Customer object
     */
    Customer createCustomer(String email, String name) throws StripeException;
    
    /**
     * Create a Stripe product
     * @param name Product name
     * @param description Product description
     * @return Stripe Product object
     */
    Product createProduct(String name, String description) throws StripeException;
    
    /**
     * Create a Stripe price
     * @param productId Stripe product ID
     * @param amount Price amount in smallest currency unit (e.g., cents)
     * @param currency Currency code (e.g., bgn)
     * @param interval Billing interval (month or year)
     * @return Stripe Price object
     */
    Price createPrice(String productId, Long amount, String currency, String interval) throws StripeException;
    
    /**
     * Create a Stripe subscription
     * @param customerId Stripe customer ID
     * @param priceId Stripe price ID
     * @param paymentMethodId Stripe payment method ID
     * @param trialPeriodDays Number of trial days (0 for no trial)
     * @return Stripe Subscription object
     */
    Subscription createSubscription(String customerId, String priceId, String paymentMethodId, Integer trialPeriodDays) throws StripeException;
    
    /**
     * Attach a payment method to a customer
     * @param paymentMethodId Stripe payment method ID
     * @param customerId Stripe customer ID
     * @return Stripe PaymentMethod object
     */
    PaymentMethod attachPaymentMethod(String paymentMethodId, String customerId) throws StripeException;
    
    /**
     * Set a payment method as the default for a customer
     * @param customerId Stripe customer ID
     * @param paymentMethodId Stripe payment method ID
     */
    void setDefaultPaymentMethod(String customerId, String paymentMethodId) throws StripeException;
    
    /**
     * Cancel a Stripe subscription
     * @param subscriptionId Stripe subscription ID
     * @return Stripe Subscription object
     */
    Subscription cancelSubscription(String subscriptionId) throws StripeException;
    
    /**
     * Retrieve a Stripe subscription
     * @param subscriptionId Stripe subscription ID
     * @return Stripe Subscription object
     */
    Subscription retrieveSubscription(String subscriptionId) throws StripeException;
}
