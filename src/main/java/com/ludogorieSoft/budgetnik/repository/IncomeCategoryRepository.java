package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.IncomeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IncomeCategoryRepository extends JpaRepository<IncomeCategory, UUID> {
    Optional<IncomeCategory> findByName(String name);
    boolean existsByName(String name);
}
