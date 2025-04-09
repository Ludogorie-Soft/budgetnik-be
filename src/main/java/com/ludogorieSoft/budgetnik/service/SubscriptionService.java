package com.ludogorieSoft.budgetnik.service;

import com.ludogorieSoft.budgetnik.dto.response.SubscriptionResponse;
import com.ludogorieSoft.budgetnik.model.Subscription;
import java.util.List;

public interface SubscriptionService {
    Subscription findByCustomerId(String customerId);
    List<SubscriptionResponse> getAllSubscriptions();
}
