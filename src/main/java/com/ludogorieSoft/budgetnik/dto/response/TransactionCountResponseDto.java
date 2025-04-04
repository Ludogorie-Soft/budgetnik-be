package com.ludogorieSoft.budgetnik.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCountResponseDto {
    private long daily;
    private long weekly;
    private long monthly;
    private long annual;
}
