package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.ExpoPushToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExponentPushTokenRepository extends JpaRepository<ExpoPushToken, UUID> {
    Optional<ExpoPushToken> findByToken(String token);
}

