package com.ludogorieSoft.budgetnik.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
  @NotBlank(message = "{error.name.notblank}")
  private String name;

  @NotNull(message = "{error.email.notnull}")
  @Email(message = "{error.email.invalid}")
  private String email;

  @NotBlank(message = "{error.password.notblank}")
  private String password;

  @NotBlank(message = "{error.confirmPassword.notblank}")
  private String confirmPassword;

  public void setEmail(String email) {
    this.email = email != null ? email.toLowerCase() : null;
  }
}
