package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Modifying
    @Query(value = "DELETE FROM user_system_messages WHERE message_id = :messageId", nativeQuery = true)
    void removeMessageFromAllUsers(@Param("messageId") UUID messageId);

    @Modifying
    @Query(value = "DELETE FROM user_promo_messages WHERE promo_message_id = :promoMessageId", nativeQuery = true)
    void removePromoMessageFromAllUsers(@Param("promoMessageId") UUID promoMessageId);

}
