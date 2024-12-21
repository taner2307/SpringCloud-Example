package com.asempty.order_service.service;

import com.asempty.order_service.client.PaymentServiceClient;
import com.asempty.order_service.client.ProductServiceClient;
import com.asempty.order_service.dto.OrderRequest;
import com.asempty.order_service.dto.OrderResponse;
import com.asempty.order_service.dto.PaymentRequest;
import com.asempty.order_service.dto.ProductResponse;
import com.asempty.order_service.entity.Order;
import com.asempty.order_service.exception.CustomException;
import com.asempty.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final PaymentServiceClient paymentServiceClient;

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
        OrderResponse orderResponse = OrderResponse.builder()
                .orderId(orderId)
                .orderDate(order.getOrderDate())
                .orderStatus(order.getOrderStatus())
                .amount(order.getTotalAmount())
                .productDetails(productServiceClient.getProductById(order.getProductId()).getBody())
                .paymentResponse(paymentServiceClient.getPaymentDetailByOrderId(orderId).getBody())
                .build();
        return orderResponse;
    }
}
