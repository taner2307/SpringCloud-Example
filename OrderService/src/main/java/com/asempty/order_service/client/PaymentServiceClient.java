package com.asempty.order_service.client;

import com.asempty.PaymentService.dto.PaymentResponse;
import com.asempty.order_service.dto.PaymentRequest;
import com.asempty.order_service.exception.CustomException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "PAYMENT-SERVICE/payment")
@CircuitBreaker(name = "external", fallbackMethod = "fallback")
public interface PaymentServiceClient {

    @PostMapping("/")
     ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest);

    @GetMapping("/{orderId}")
     ResponseEntity<PaymentResponse> getPaymentDetailByOrderId(@PathVariable("orderId") long orderId);

    default ResponseEntity<Void> fallback(Exception e) {
        throw new CustomException("Payment Service is unavailable", "UNAVAILABLE", 500);
    }
}
