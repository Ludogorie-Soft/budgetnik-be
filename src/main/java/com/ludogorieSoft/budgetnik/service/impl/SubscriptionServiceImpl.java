package com.ludogorieSoft.budgetnik.service.impl;

import com.ludogorieSoft.budgetnik.dto.response.SubscriptionResponse;
import com.ludogorieSoft.budgetnik.model.Subscription;
import com.ludogorieSoft.budgetnik.repository.SubscriptionRepository;
import com.ludogorieSoft.budgetnik.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

  private final SubscriptionRepository subscriptionRepository;
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
}
