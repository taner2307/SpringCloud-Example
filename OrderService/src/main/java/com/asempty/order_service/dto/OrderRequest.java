package com.asempty.order_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    private long productId;

    private long quantity;

    private Instant orderDate;

    private String orderStatus;

    private long totalAmount;

    private PaymentMode paymentMode;
}
