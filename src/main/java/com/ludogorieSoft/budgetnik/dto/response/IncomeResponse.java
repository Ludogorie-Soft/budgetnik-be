package com.ludogorieSoft.budgetnik.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class IncomeResponse {
    private UUID id;
    private LocalDate creationDate;
    private BigDecimal sum;
}
