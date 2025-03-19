package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.SystemMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SystemMessageRepository extends JpaRepository<SystemMessage, UUID> {}
