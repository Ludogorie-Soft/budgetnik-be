package com.ludogorieSoft.budgetnik.dto.request;

import com.ludogorieSoft.budgetnik.model.enums.Regularity;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class ExpenseRequestDto {
  private Type type;
  private Regularity regularity;
  private LocalDate creationDate;
  private String category;
  private String subcategory;
  private BigDecimal sum;
  private String description;
  private UUID ownerId;
  private UUID relatedExpenseId;
}
