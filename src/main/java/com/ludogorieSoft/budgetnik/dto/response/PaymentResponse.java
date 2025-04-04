package com.ludogorieSoft.budgetnik.dto.response;

import lombok.Data;

@Data
public class PaymentResponse {
    private String paymentIntent;
    private String ephemeralKey;
    private String customerId;
    private String publishableKey;
}
