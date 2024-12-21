package com.asempty.order_service.dto;

import com.asempty.PaymentService.dto.PaymentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private long orderId;

    private Instant orderDate;

    private String orderStatus;

    private long amount;

    private ProductResponse productDetails;

    private PaymentResponse paymentResponse;
}
