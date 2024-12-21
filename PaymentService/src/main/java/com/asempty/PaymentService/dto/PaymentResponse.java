package com.asempty.PaymentService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private long paymentId;
    private long orderId;
    private String status;
    private long amount;
    private PaymentMode paymentMode;
    private Instant paymentDate;
}
