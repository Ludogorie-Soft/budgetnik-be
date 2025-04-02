package com.ludogorieSoft.budgetnik.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SystemMessageRequestDto {
  @NotBlank(message = "{error.title.notblank}")
  @Size(min = 1, message = "{error.title.size}")
  private String title;

  @NotBlank(message = "{error.body.notblank}")
  @Size(min = 1, message = "{error.body.size}")
  private String body;
}
