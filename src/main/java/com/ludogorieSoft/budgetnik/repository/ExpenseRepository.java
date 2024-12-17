package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.Expense;
import com.ludogorieSoft.budgetnik.model.ExpenseCategory;
import com.ludogorieSoft.budgetnik.model.IncomeCategory;
import com.ludogorieSoft.budgetnik.model.User;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {
  List<Expense> findAllByOwner(User owner);

  List<Expense> findAllByOwnerAndType(User owner, Type type);

  @Query(
      value = "SELECT COALESCE(SUM(sum), 0) FROM expenses WHERE owner_id = :userId",
      nativeQuery = true)
  BigDecimal calculateTotalSumByUserId(@Param("userId") UUID userId);

  @Query(
      "SELECT COALESCE(SUM(e.sum), 0) FROM Expense e WHERE e.owner.id = :userId "
          + "AND (:category IS NULL OR e.category = :category)")
  BigDecimal calculateTotalSumByUserIdAndCategory(
      @Param("userId") UUID userId, @Param("category") ExpenseCategory category);

  @Query(
      "SELECT COALESCE(SUM(e.sum), 0) FROM Expense e WHERE e.owner.id = :userId "
          + "AND (:type IS NULL OR e.type = :type)")
  BigDecimal calculateSumOfUserExpensesByType(
      @Param("userId") UUID userId, @Param("type") Type type);

  @Query(
      "SELECT e FROM Expense e WHERE e.owner = :owner AND e.date >= :startDate AND e.date <= :endDate")
  List<Expense> findExpensesForPeriod(
      @Param("owner") User owner,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Query(
          "SELECT COALESCE(SUM(e.sum), 0) FROM Expense e WHERE e.owner = :user "
                  + "AND (:category IS NULL OR e.category = :category) AND e.date >= :startDate AND e.date <= :endDate")
  BigDecimal calculateSumOfExpensesByCategory(
          @Param("user") User user, @Param("category") ExpenseCategory category, @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate);
}
