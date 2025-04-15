package com.ludogorieSoft.budgetnik.dto.response;

import com.ludogorieSoft.budgetnik.model.enums.Role;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class UserResponse {
    private UUID id;
    private String name;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private String appRating;
}
