package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.Message;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PromoMessageRepository extends JpaRepository<Message, UUID> {
  List<Message> findByIdIn(List<UUID> ids);
}
