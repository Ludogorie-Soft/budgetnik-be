package com.ludogorieSoft.budgetnik.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.Data;

@Data
public class MessageRequestDto {
  @NotNull(message = "{error.date.notnull}")
  @FutureOrPresent(message = "{error.date.futureOrPresent}")
  private LocalDate date;

  @NotBlank(message = "{error.title.notblank}")
  private String title;

  @NotNull(message = "{error.fromDate.notnull}")
  @PastOrPresent(message = "{error.fromDate.pastOrPresent}")
  private LocalDate fromDate;

  @NotNull(message = "{error.toDate.notnull}")
  @PastOrPresent(message = "{error.toDate.pastOrPresent}")
  private LocalDate toDate;

  @NotBlank(message = "{error.body.notblank}")
  private String body;

  @Min(value = 0, message = "{error.discount.min}")
  private double discount;

  @Pattern(regexp = "^[A-Za-z0-9]{6,}$", message = "{error.promoCode.invalid}")
  private String promoCode;

  private String link;
}
