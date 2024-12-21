package com.asempty.order_service.service;

import com.asempty.PaymentService.dto.PaymentMode;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceImplTest {

    @Mock
    public OrderRepository orderRepository;
    @Mock
    public ProductServiceClient productServiceClient;
    @Mock
    public PaymentServiceClient paymentServiceClient;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    OrderService orderService = new OrderServiceImpl();

    @DisplayName("Get Order - Success Scenario")
    @Test
    void test_when_order_success() {
        //Mocking
        Order order = getMockOrder();

        //Actual
        when(orderRepository.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(order));
        when(restTemplate.getForObject(
                "http://PRODUCT-SERVICE/product/" + order.getProductId(),
                ProductResponse.class
        )).thenReturn(getMockProductResponse());

        when(restTemplate.getForObject(
                "http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                PaymentResponse.class
        )).thenReturn(getMockPaymentResponse());
        OrderResponse orderResponse = orderService.getOrderDetail(1);
        orderResponse.setProductDetails(null);
        orderResponse.setPaymentResponse(null);

        //Verification
        verify(orderRepository,times(1)).findById(anyLong());

        //Assert
        Assertions.assertNotNull(orderResponse);
        assertEquals(order.getId(),orderResponse.getOrderId());
    }

    @DisplayName("Get Order - Failure Scenario")
    @Test
    void test_when_get_order_not_found_then_not_found() {
        when(orderRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(null));

        CustomException customException = assertThrows(CustomException.class, () -> orderService.getOrderDetail(1));
        assertEquals("NOT_FOUND", customException.getErrorCode());
        assertEquals(404, customException.getStatus());

        verify(orderRepository, times(1))
                .findById(anyLong());
    }

    @DisplayName("Place Order - Success Scenario")
    @Test
    void test_when_place_order_success() {
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productServiceClient.reduceQuantity(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(paymentServiceClient.doPayment(any(PaymentRequest.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2))
                .save(any());
        verify(productServiceClient, times(1))
                .reduceQuantity(anyLong(),anyLong());
        verify(paymentServiceClient, times(1))
                .doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);

    }

    @DisplayName("Place Order - Payment Failed Scenario")
    @Test
    void test_when_place_order_payment_fails_then_order_place() {
        Order order = getMockOrder();
        OrderRequest orderRequest = getMockOrderRequest();

        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);
        when(productServiceClient.reduceQuantity(anyLong(), anyLong()))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(paymentServiceClient.doPayment(any(PaymentRequest.class)))
                .thenThrow(new RuntimeException());
        long orderId = orderService.placeOrder(orderRequest);

        verify(orderRepository, times(2))
                .save(any());
        verify(productServiceClient, times(1))
                .reduceQuantity(anyLong(),anyLong());
        verify(paymentServiceClient, times(1))
                .doPayment(any(PaymentRequest.class));

        assertEquals(order.getId(), orderId);
    }
    private Order getMockOrder() {
        return Order.builder()
                .orderStatus("PLACED")
                .orderDate(Instant.now())
                .id(1L)
                .totalAmount(100)
                .quantity(200)
                .productId(1)
                .build();

    }

    private PaymentResponse getMockPaymentResponse() {
        return PaymentResponse.builder()
                .paymentId(1)
                .paymentDate(Instant.now())
                .paymentMode(PaymentMode.CASH)
                .amount(200)
                .orderId(1)
                .status("ACCEPTED")
                .build();
    }

    private ProductResponse getMockProductResponse() {
        return ProductResponse.builder()
                .productId(2)
                .productName("iPhone")
                .price(100)
                .quantity(200)
                .build();
    }

    private OrderRequest getMockOrderRequest() {
        return OrderRequest.builder()
                .productId(1)
                .quantity(10)
                .paymentMode(com.asempty.order_service.dto.PaymentMode.CASH)
                .totalAmount(100)
                .build();
    }
}