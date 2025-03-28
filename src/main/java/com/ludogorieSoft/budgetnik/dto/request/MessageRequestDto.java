package com.ludogorieSoft.budgetnik.dto.request;

import java.time.LocalDate;
import java.util.Date;
import lombok.Data;

@Data
public class MessageRequestDto {
  private LocalDate date;
  private String title;
  private LocalDate fromDate;
  private LocalDate toDate;
  private String body;
  private double discount;
  private String promoCode;
  private String link;
}
