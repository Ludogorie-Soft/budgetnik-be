package com.ludogorieSoft.budgetnik.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactFormRequestDto {
  @NotBlank(message = "{error.email.notnull}")
  @Email(message = "{error.email.invalid}")
  private String email;

  @NotBlank(message = "{error.title.notblank}")
  private String title;

  @NotBlank(message = "{error.body.notblank}")
  private String message;
}
