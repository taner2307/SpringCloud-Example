package com.asempty.ApiGateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallBackController {

    @GetMapping("/orderServiceFallback")
    public String orderServiceFallback() {
        return "Order service is down";
    }

    @GetMapping("/paymentServiceFallback")
    public String paymentServiceFallback() {
        return "Order service is down";
    }

    @GetMapping("/productServiceFallback")
    public String productServiceFallback() {
        return "Product service is down";
    }
}
