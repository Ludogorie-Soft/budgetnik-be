package com.ludogorieSoft.budgetnik.controller;

import com.ludogorieSoft.budgetnik.dto.request.SubscriptionPlanRequest;
import com.ludogorieSoft.budgetnik.dto.response.SubscriptionPlanResponse;
import com.ludogorieSoft.budgetnik.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscription-plans")
@RequiredArgsConstructor
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    @GetMapping
    public ResponseEntity<List<SubscriptionPlanResponse>> getAllPlans() {
        // Get the current locale from the request
        Locale locale = LocaleContextHolder.getLocale();
        String language = locale.getLanguage(); // "en", "bg", etc.

        List<SubscriptionPlanResponse> plans = subscriptionPlanService.getActivePlans();

        // Log the locale and language for debugging
        System.out.println("Current locale: " + locale + ", language: " + language);

        return new ResponseEntity<>(plans, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionPlanResponse> getPlanById(@PathVariable UUID id) {
        SubscriptionPlanResponse plan = subscriptionPlanService.getPlanById(id);
        return new ResponseEntity<>(plan, HttpStatus.OK);
    }

    @GetMapping("/interval/{interval}")
    public ResponseEntity<List<SubscriptionPlanResponse>> getPlansByInterval(@PathVariable String interval) {
        List<SubscriptionPlanResponse> plans = subscriptionPlanService.getPlansByInterval(interval);
        return new ResponseEntity<>(plans, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionPlanResponse> createPlan(@Valid @RequestBody SubscriptionPlanRequest request) {
        SubscriptionPlanResponse plan = subscriptionPlanService.createPlan(request);
        return new ResponseEntity<>(plan, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubscriptionPlanResponse> updatePlan(
            @PathVariable UUID id,
            @Valid @RequestBody SubscriptionPlanRequest request) {
        SubscriptionPlanResponse plan = subscriptionPlanService.updatePlan(id, request);
        return new ResponseEntity<>(plan, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePlan(@PathVariable UUID id) {
        subscriptionPlanService.deletePlan(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
