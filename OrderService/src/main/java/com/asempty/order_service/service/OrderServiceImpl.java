package com.asempty.order_service.service;

import com.asempty.PaymentService.dto.PaymentResponse;
import com.asempty.order_service.client.PaymentServiceClient;
import com.asempty.order_service.client.ProductServiceClient;
import com.asempty.order_service.dto.OrderRequest;
import com.asempty.order_service.dto.OrderResponse;
import com.asempty.order_service.dto.PaymentRequest;
import com.asempty.order_service.dto.ProductResponse;
import com.asempty.order_service.entity.Order;
import com.asempty.order_service.exception.CustomException;
import com.asempty.order_service.repository.OrderRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
@NoArgsConstructor
public class OrderServiceImpl implements OrderService {

    @Autowired
    public OrderRepository orderRepository;
    @Autowired
    public ProductServiceClient productServiceClient;
    @Autowired
    public PaymentServiceClient paymentServiceClient;

    @Autowired(required = false)
    public RestTemplate restTemplate;


    @Override
    public long placeOrder(OrderRequest orderRequest) {
        log.info("Place order request {}", orderRequest);

        productServiceClient.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());
        Order order = Order.builder()
                .totalAmount(orderRequest.getTotalAmount())
                .orderDate(Instant.now())
                .orderStatus("CREATED")
                .quantity(orderRequest.getQuantity())
                .productId(orderRequest.getProductId())
                .build();
        order = orderRepository.save(order);

        log.info("Called Payment service");
        PaymentRequest paymentRequest = PaymentRequest.builder()
                        .orderId(order.getId())
                .paymentMode(orderRequest.getPaymentMode())
                .amount(orderRequest.getTotalAmount()).build();
        String orderStatus = null;
        try {
            paymentServiceClient.doPayment(paymentRequest);
            log.info("Payment success");
            orderStatus = "PLACED";
        }catch (Exception e) {
            log.error("Error occured in payment");
            orderStatus = "PAYMENT_FAILED";
        }
        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order placed {}", order);
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetail(long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException("Order not found","NOT_FOUND", 404));

        ProductResponse productResponse
                = restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class
        );

        PaymentResponse paymentResponse
                = restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                PaymentResponse.class
        );

        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(orderId)
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .amount(order.getTotalAmount())
                .productDetails(productResponse)
                .paymentResponse(paymentResponse)
                .build();
        return orderResponse;
    }
}
