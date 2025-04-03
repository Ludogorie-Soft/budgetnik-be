package com.ludogorieSoft.budgetnik.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.Data;

@Data
public class MessageRequestDto {
  @NotNull(message = "{error.date.notnull}")
  private LocalDate date;

  @NotBlank(message = "{error.title.notblank}")
  private String title;

  @NotNull(message = "{error.fromDate.notnull}")
  private LocalDate fromDate;

  @NotNull(message = "{error.toDate.notnull}")
  private LocalDate toDate;

  @NotBlank(message = "{error.body.notblank}")
  private String body;

  @Min(value = 0, message = "{error.discount.min}")
  private double discount;

  private String promoCode;

  private String link;
}
