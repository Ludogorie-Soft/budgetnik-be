package com.ludogorieSoft.budgetnik.dto.response;

import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public class CategoryResponseDto {
  private UUID id;
  private String name;
  private String bgName;
  private String translations;
  private List<SubcategoryResponse> subcategories;
}
