package com.asempty.PaymentService.service;

import com.asempty.PaymentService.dto.PaymentRequest;
import com.asempty.PaymentService.dto.PaymentResponse;

public interface PaymentService {
    Long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailByOrderId(long orderId);
}
