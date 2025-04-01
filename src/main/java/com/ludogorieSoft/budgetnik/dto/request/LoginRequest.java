package com.ludogorieSoft.budgetnik.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
  @NotBlank(message = "{error.email.notnull}")
  @Email(message = "{error.email.invalid}")
  private String email;

  @NotBlank(message = "{error.password.notblank}")
  private String password;
}
