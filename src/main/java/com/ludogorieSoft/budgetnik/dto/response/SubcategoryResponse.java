package com.ludogorieSoft.budgetnik.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class SubcategoryResponse {
    private UUID id;
    private String name;
    private String bgName;
    private String translations;
}
