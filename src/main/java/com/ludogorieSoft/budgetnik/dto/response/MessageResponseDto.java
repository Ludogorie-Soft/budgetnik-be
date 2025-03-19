package com.ludogorieSoft.budgetnik.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Data
public class MessageResponseDto {
    private UUID id;
    private LocalDate date;
    private String title;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String message;
    private double discount;
    private String link;
}
