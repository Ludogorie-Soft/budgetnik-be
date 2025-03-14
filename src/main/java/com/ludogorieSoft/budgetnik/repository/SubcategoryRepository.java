package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.ExpenseCategory;
import com.ludogorieSoft.budgetnik.model.IncomeCategory;
import com.ludogorieSoft.budgetnik.model.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, UUID> {
    Optional<Subcategory> findByNameAndIncomeCategory(String name, IncomeCategory incomeCategory);
    Optional<Subcategory> findByNameAndExpenseCategory(String name, ExpenseCategory incomeCategory);
}
