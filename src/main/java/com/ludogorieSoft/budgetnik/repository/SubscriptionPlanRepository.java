package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, UUID> {
    List<SubscriptionPlan> findByActiveTrue();
    Optional<SubscriptionPlan> findByStripePriceId(String stripePriceId);
    List<SubscriptionPlan> findByInterval(String interval);
}
