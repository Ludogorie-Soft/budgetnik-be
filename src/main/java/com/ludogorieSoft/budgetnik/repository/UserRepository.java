package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

//    @Query("SELECT u FROM User u WHERE lower(u.name) LIKE lower(concat('%', :searchTerm, '%')) OR lower(u.email) LIKE lower(concat('%', :searchTerm, '%'))")
//    Page<User> findByNameOrEmailContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);

    Page<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email, Pageable pageable);


    @Modifying
    @Query(value = "DELETE FROM user_system_messages WHERE system_message_id = :messageId", nativeQuery = true)
    void removeMessageFromAllUsers(@Param("messageId") UUID messageId);

    @Modifying
    @Query(value = "DELETE FROM user_promo_messages WHERE promo_message_id = :promoMessageId", nativeQuery = true)
    void removePromoMessageFromAllUsers(@Param("promoMessageId") UUID promoMessageId);

    @Query("SELECT u FROM User u ORDER BY u.createdAt DESC")
    List<User> findTopTenByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT u FROM User u ORDER BY u.lastLogin DESC")
    List<User> findTopTenByOrderByLastLoginDesc(Pageable pageable);

}
