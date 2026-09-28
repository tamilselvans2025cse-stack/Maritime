package com.example.maritime.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PaymentRequest {

    // For customer payments: invoiceId + customerId
    private Long invoiceId;
    private Long customerId;

    // For vendor payments: vendorBillId + vendorId
    private Long vendorBillId;
    private Long vendorId;

    @NotNull
    @Positive
    private BigDecimal amount;

    private String paymentType; // BANK or CASH
}
