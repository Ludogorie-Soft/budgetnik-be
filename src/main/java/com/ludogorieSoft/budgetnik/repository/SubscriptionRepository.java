package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {}
