package com.ludogorieSoft.budgetnik.dto.request;

import com.ludogorieSoft.budgetnik.model.enums.Regularity;
import com.ludogorieSoft.budgetnik.model.enums.Type;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Data;

@Data
public class ExpenseRequestDto {
  @NotNull(message = "{error.type.notnull}")
  private Type type;

  @NotNull(message = "{error.regularity.notnull}")
  private Regularity regularity;

  @NotNull(message = "{error.creationDate.notnull}")
  private LocalDate creationDate;

  @NotBlank(message = "{error.category.notblank}")
  private String category;

  private String subcategory;

  @NotNull(message = "{error.sum.notnull}")
  @DecimalMin(value = "0.01", inclusive = true, message = "{error.sum.min}")
  private BigDecimal sum;

  private String description;

  @NotNull(message = "{error.ownerId.notnull}")
  private UUID ownerId;

  private UUID relatedExpenseId;
}
