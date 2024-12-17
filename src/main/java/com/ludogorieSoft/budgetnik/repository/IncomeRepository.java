package com.ludogorieSoft.budgetnik.repository;

import com.ludogorieSoft.budgetnik.model.Income;
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
public interface IncomeRepository extends JpaRepository<Income, UUID> {
  List<Income> findAllByOwner(User owner);

  List<Income> findAllByOwnerAndType(User owner, Type type);

  @Query(
      value = "SELECT COALESCE(SUM(sum), 0) FROM incomes WHERE owner_id = :userId",
      nativeQuery = true)
  BigDecimal calculateTotalSumByUserId(@Param("userId") UUID userId);

  @Query(
      "SELECT COALESCE(SUM(i.sum), 0) FROM Income i WHERE i.owner.id = :userId "
          + "AND (:category IS NULL OR i.category = :category)")
  BigDecimal calculateTotalSumByUserIdAndCategory(
      @Param("userId") UUID userId, @Param("category") IncomeCategory category);

  @Query(
      "SELECT COALESCE(SUM(i.sum), 0) FROM Income i WHERE i.owner.id = :userId "
          + "AND (:type IS NULL OR i.type = :type)")
  BigDecimal calculateSumOfUserIncomesByType(
      @Param("userId") UUID userId, @Param("type") Type type);

  @Query(
      "SELECT i FROM Income i WHERE i.owner = :owner AND i.date >= :startDate AND i.date <= :endDate")
  List<Income> findIncomesForPeriod(
      @Param("owner") User owner,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);

  @Query(
          "SELECT COALESCE(SUM(i.sum), 0) FROM Income i WHERE i.owner = :user "
                  + "AND (:category IS NULL OR i.category = :category) AND i.date >= :startDate AND i.date <= :endDate")
  BigDecimal calculateSumOfIncomesByCategory(
          @Param("user") User user, @Param("category") IncomeCategory category, @Param("startDate") LocalDate startDate,
          @Param("endDate") LocalDate endDate);
}
