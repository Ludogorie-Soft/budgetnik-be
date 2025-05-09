package com.ludogorieSoft.budgetnik.service;

import com.ludogorieSoft.budgetnik.dto.request.SubscriptionPlanRequest;
import com.ludogorieSoft.budgetnik.dto.response.SubscriptionPlanResponse;
import com.ludogorieSoft.budgetnik.model.SubscriptionPlan;

import java.util.List;
import java.util.UUID;

public interface SubscriptionPlanService {
    /**
     * Create a new subscription plan
     * @param request SubscriptionPlanRequest
     * @return SubscriptionPlanResponse
     */
    SubscriptionPlanResponse createPlan(SubscriptionPlanRequest request);
    
    /**
     * Get all subscription plans
     * @return List of SubscriptionPlanResponse
     */
    List<SubscriptionPlanResponse> getAllPlans();
    
    /**
     * Get all active subscription plans
     * @return List of SubscriptionPlanResponse
     */
    List<SubscriptionPlanResponse> getActivePlans();
    
    /**
     * Get subscription plans by interval
     * @param interval Billing interval (month or year)
     * @return List of SubscriptionPlanResponse
     */
    List<SubscriptionPlanResponse> getPlansByInterval(String interval);
    
    /**
     * Get a subscription plan by ID
     * @param id Subscription plan ID
     * @return SubscriptionPlanResponse
     */
    SubscriptionPlanResponse getPlanById(UUID id);
    
    /**
     * Get a subscription plan by Stripe price ID
     * @param stripePriceId Stripe price ID
     * @return SubscriptionPlan
     */
    SubscriptionPlan findByStripePriceId(String stripePriceId);
    
    /**
     * Update a subscription plan
     * @param id Subscription plan ID
     * @param request SubscriptionPlanRequest
     * @return SubscriptionPlanResponse
     */
    SubscriptionPlanResponse updatePlan(UUID id, SubscriptionPlanRequest request);
    
    /**
     * Delete a subscription plan
     * @param id Subscription plan ID
     */
    void deletePlan(UUID id);
    
    /**
     * Initialize default subscription plans
     */
    void initializeDefaultPlans();
}
