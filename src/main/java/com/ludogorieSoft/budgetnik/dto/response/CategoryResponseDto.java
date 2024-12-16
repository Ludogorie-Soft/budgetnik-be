package com.ludogorieSoft.budgetnik.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class CategoryResponseDto {
    private UUID id;
    private String name;
    private String bgName;
}
