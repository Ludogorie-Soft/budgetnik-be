package com.ludogorieSoft.budgetnik.dto.request;

import com.ludogorieSoft.budgetnik.model.enums.Regularity;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class IncomeRequestDto {
  private Type type;
  private Regularity regularity;
  private LocalDate date;
  private String category;
  private BigDecimal sum;
  private String oneTimeIncome;
  private UUID ownerId;
}
