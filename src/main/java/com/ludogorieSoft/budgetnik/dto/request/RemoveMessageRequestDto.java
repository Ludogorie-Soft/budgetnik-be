package com.ludogorieSoft.budgetnik.dto.request;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RemoveMessageRequestDto {
    List<UUID> messageIds;
}
