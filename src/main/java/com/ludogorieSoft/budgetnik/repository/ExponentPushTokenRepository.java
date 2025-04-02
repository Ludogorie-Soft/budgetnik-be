package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.ExpoPushToken;
import com.ludogorieSoft.budgetnik.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExponentPushTokenRepository extends JpaRepository<ExpoPushToken, UUID> {
    Optional<ExpoPushToken> findByToken(String token);
    Optional<ExpoPushToken> findByTokenAndUser(String token, User user);
}

