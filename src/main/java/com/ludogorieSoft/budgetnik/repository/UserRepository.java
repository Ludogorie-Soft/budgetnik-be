package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
  Optional<User> findByEmailIgnoreCase(String email);

  boolean existsByEmailIgnoreCase(String email);

  Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
      String name, String email, Pageable pageable);

  @Modifying
  @Query(
      value = "DELETE FROM user_system_messages WHERE system_message_id = :messageId",
      nativeQuery = true)
  void removeMessageFromAllUsers(@Param("messageId") UUID messageId);

  @Modifying
  @Query(
      value = "DELETE FROM user_promo_messages WHERE promo_message_id = :promoMessageId",
      nativeQuery = true)
  void removePromoMessageFromAllUsers(@Param("promoMessageId") UUID promoMessageId);

  List<User> findTop10ByOrderByCreatedAtDesc();

  List<User> findTop10ByOrderByLastLoginDesc();

  User findByCustomerId(String customerId);
}
