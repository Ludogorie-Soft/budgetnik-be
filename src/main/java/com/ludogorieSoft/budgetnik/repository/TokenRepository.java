package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.Token;
import com.ludogorieSoft.budgetnik.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {
    List<Token> findAllByUser(User user);
    List<Token> findAllByExpiredTrue();
    Optional<Token> findByToken(String token);
}
