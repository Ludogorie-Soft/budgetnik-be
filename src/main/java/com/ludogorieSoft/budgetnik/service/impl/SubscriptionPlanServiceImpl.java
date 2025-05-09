package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.request.SubscriptionPlanRequest;
import com.ludogorieSoft.budgetnik.dto.response.SubscriptionPlanResponse;
import com.ludogorieSoft.budgetnik.model.SubscriptionPlan;
import com.ludogorieSoft.budgetnik.repository.SubscriptionPlanRepository;
import com.ludogorieSoft.budgetnik.service.SubscriptionPlanService;
import com.ludogorieSoft.budgetnik.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.model.Product;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionPlanServiceImpl implements SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final ModelMapper modelMapper;
    private final StripeService stripeService;

    @Value("${stripe.currency}")
    private String currency;

    @Override
    @Transactional
    public SubscriptionPlanResponse createPlan(SubscriptionPlanRequest request) {
        try {
            // Create Stripe product
            Product product = stripeService.createProduct(request.getName(), request.getDescription());

            // Create Stripe price
            Price price = stripeService.createPrice(
                    product.getId(),
                    request.getAmount(),
                    request.getCurrency(),
                    request.getInterval()
            );

            // Create subscription plan
            SubscriptionPlan plan = new SubscriptionPlan();
            plan.setName(request.getName());
            plan.setDescription(request.getDescription());
            plan.setAmount(request.getAmount());
            plan.setCurrency(request.getCurrency());
            plan.setInterval(request.getInterval());
            plan.setFeatures(request.getFeatures());
            plan.setStripeProductId(product.getId());
            plan.setStripePriceId(price.getId());
            plan.setActive(true);
            plan.setTranslations(request.getTranslations());

            SubscriptionPlan savedPlan = subscriptionPlanRepository.save(plan);
            return modelMapper.map(savedPlan, SubscriptionPlanResponse.class);
        } catch (StripeException e) {
            log.error("Error creating subscription plan in Stripe", e);
            throw new RuntimeException("Error creating subscription plan: " + e.getMessage());
        }
    }

    @Override
    public List<SubscriptionPlanResponse> getAllPlans() {
        return subscriptionPlanRepository.findAll().stream()
                .map(plan -> modelMapper.map(plan, SubscriptionPlanResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionPlanResponse> getActivePlans() {
        return subscriptionPlanRepository.findByActiveTrue().stream()
                .map(plan -> modelMapper.map(plan, SubscriptionPlanResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<SubscriptionPlanResponse> getPlansByInterval(String interval) {
        return subscriptionPlanRepository.findByInterval(interval).stream()
                .map(plan -> modelMapper.map(plan, SubscriptionPlanResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public SubscriptionPlanResponse getPlanById(UUID id) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription plan not found with id: " + id));
        return modelMapper.map(plan, SubscriptionPlanResponse.class);
    }

    @Override
    public SubscriptionPlan findByStripePriceId(String stripePriceId) {
        return subscriptionPlanRepository.findByStripePriceId(stripePriceId)
                .orElseThrow(() -> new RuntimeException("Subscription plan not found with Stripe price ID: " + stripePriceId));
    }

    @Override
    @Transactional
    public SubscriptionPlanResponse updatePlan(UUID id, SubscriptionPlanRequest request) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription plan not found with id: " + id));

        plan.setName(request.getName());
        plan.setDescription(request.getDescription());
        plan.setFeatures(request.getFeatures());

        // Update translations if provided
        if (request.getTranslations() != null) {
            plan.setTranslations(request.getTranslations());
        }

        SubscriptionPlan updatedPlan = subscriptionPlanRepository.save(plan);
        return modelMapper.map(updatedPlan, SubscriptionPlanResponse.class);
    }

    @Override
    @Transactional
    public void deletePlan(UUID id) {
        SubscriptionPlan plan = subscriptionPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription plan not found with id: " + id));

        plan.setActive(false);
        subscriptionPlanRepository.save(plan);
    }

    @Override
    @PostConstruct
    @Transactional
    public void initializeDefaultPlans() {
        try {
            // Check for existing plans
            List<SubscriptionPlan> monthlyPlans = subscriptionPlanRepository.findByInterval("month");
            List<SubscriptionPlan> yearlyPlans = subscriptionPlanRepository.findByInterval("year");

            // Handle monthly plan
            if (monthlyPlans.isEmpty()) {
                // Create new monthly plan
                SubscriptionPlanRequest monthlyPlan = new SubscriptionPlanRequest();
                monthlyPlan.setName("Monthly Plan");
                monthlyPlan.setDescription("Access to all premium features with monthly billing");
                monthlyPlan.setAmount(999L); // 9.99 BGN
                monthlyPlan.setCurrency(currency);
                monthlyPlan.setInterval("month");
                monthlyPlan.setFeatures("All premium features,Priority support,Unlimited transactions");
                createPlan(monthlyPlan);
                log.info("Created new monthly subscription plan");
            } else {
                // Update existing monthly plan if it has placeholder Stripe IDs
                SubscriptionPlan existingMonthlyPlan = monthlyPlans.get(0);
                if (existingMonthlyPlan.getStripeProductId() == null ||
                    existingMonthlyPlan.getStripeProductId().startsWith("placeholder_") ||
                    existingMonthlyPlan.getStripePriceId() == null ||
                    existingMonthlyPlan.getStripePriceId().startsWith("placeholder_")) {

                    try {
                        // Create Stripe product
                        Product product = stripeService.createProduct(
                                existingMonthlyPlan.getName(),
                                existingMonthlyPlan.getDescription());

                        // Create Stripe price
                        Price price = stripeService.createPrice(
                                product.getId(),
                                existingMonthlyPlan.getAmount(),
                                existingMonthlyPlan.getCurrency(),
                                existingMonthlyPlan.getInterval()
                        );

                        // Update plan with real Stripe IDs
                        existingMonthlyPlan.setStripeProductId(product.getId());
                        existingMonthlyPlan.setStripePriceId(price.getId());
                        subscriptionPlanRepository.save(existingMonthlyPlan);
                        log.info("Updated existing monthly subscription plan with Stripe IDs");
                    } catch (StripeException e) {
                        log.error("Error updating monthly plan with Stripe IDs", e);
                    }
                }
            }

            // Handle yearly plan
            if (yearlyPlans.isEmpty()) {
                // Create new yearly plan
                SubscriptionPlanRequest annualPlan = new SubscriptionPlanRequest();
                annualPlan.setName("Annual Plan");
                annualPlan.setDescription("Access to all premium features with annual billing (save 25%)");
                annualPlan.setAmount(9099L); // 90.99 BGN
                annualPlan.setCurrency(currency);
                annualPlan.setInterval("year");
                annualPlan.setFeatures("All premium features,Priority support,Unlimited transactions,Annual discount");
                createPlan(annualPlan);
                log.info("Created new annual subscription plan");
            } else {
                // Update existing yearly plan if it has placeholder Stripe IDs
                SubscriptionPlan existingYearlyPlan = yearlyPlans.get(0);
                if (existingYearlyPlan.getStripeProductId() == null ||
                    existingYearlyPlan.getStripeProductId().startsWith("placeholder_") ||
                    existingYearlyPlan.getStripePriceId() == null ||
                    existingYearlyPlan.getStripePriceId().startsWith("placeholder_")) {

                    try {
                        // Create Stripe product
                        Product product = stripeService.createProduct(
                                existingYearlyPlan.getName(),
                                existingYearlyPlan.getDescription());

                        // Create Stripe price
                        Price price = stripeService.createPrice(
                                product.getId(),
                                existingYearlyPlan.getAmount(),
                                existingYearlyPlan.getCurrency(),
                                existingYearlyPlan.getInterval()
                        );

                        // Update plan with real Stripe IDs
                        existingYearlyPlan.setStripeProductId(product.getId());
                        existingYearlyPlan.setStripePriceId(price.getId());
                        subscriptionPlanRepository.save(existingYearlyPlan);
                        log.info("Updated existing yearly subscription plan with Stripe IDs");
                    } catch (StripeException e) {
                        log.error("Error updating yearly plan with Stripe IDs", e);
                    }
                }
            }

            log.info("Subscription plans initialization completed");
        } catch (Exception e) {
            log.error("Error during subscription plans initialization", e);
        }
    }
}
