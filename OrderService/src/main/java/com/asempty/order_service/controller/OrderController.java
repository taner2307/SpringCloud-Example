package com.asempty.order_service.controller;

import com.asempty.order_service.dto.OrderRequest;
import com.asempty.order_service.dto.OrderResponse;
import com.asempty.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Log4j2
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/placeOrder")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest) {
        long orderId = orderService.placeOrder(orderRequest);
        log.info("Order ID: {}", orderId);
        return new ResponseEntity<>(orderId, HttpStatus.CREATED);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetail(@PathVariable("orderId") long orderId) {
        OrderResponse orderResponse = orderService.getOrderDetail(orderId);
        log.info("Order Detail service call by Id: {}", orderId);
        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }
}
