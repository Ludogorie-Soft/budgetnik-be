package com.ludogorieSoft.budgetnik.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class SystemMessageResponseDto {
    private UUID id;
    private LocalDate date;
    private String title;
    private String body;
}
