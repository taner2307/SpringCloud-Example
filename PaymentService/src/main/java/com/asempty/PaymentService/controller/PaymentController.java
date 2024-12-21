package com.asempty.PaymentService.controller;

import com.asempty.PaymentService.dto.PaymentRequest;
import com.asempty.PaymentService.dto.PaymentResponse;
import com.asempty.PaymentService.service.PaymentService;
import jakarta.ws.rs.Path;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/")
    public ResponseEntity<Long> doPayment(@RequestBody PaymentRequest paymentRequest) {
        return new ResponseEntity<>(paymentService.doPayment(paymentRequest),
                HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> getPaymentDetailByOrderId(@PathVariable("orderId") long orderId) {
        return new ResponseEntity<>(paymentService.getPaymentDetailByOrderId(orderId),
                HttpStatus.OK);
    }
}
