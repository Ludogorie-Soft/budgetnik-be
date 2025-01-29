package com.ludogorieSoft.budgetnik.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ludogorieSoft.budgetnik.model.enums.Regularity;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class IncomeResponseDto {
  private UUID id;
  private Type type;
  private Regularity regularity;
  private LocalDate creationDate;
  private CategoryResponseDto incomeCategory;
  private BigDecimal sum;
  private String oneTimeIncome;
  private LocalDate dueDate;
  private UUID relatedIncomeId;
  private boolean autoCreate;
}
