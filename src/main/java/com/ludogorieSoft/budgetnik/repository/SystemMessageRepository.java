package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.SystemMessage;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemMessageRepository extends JpaRepository<SystemMessage, UUID> {
  List<SystemMessage> findByIdIn(List<UUID> ids);
}
