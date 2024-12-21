package com.asempty.order_service.service;

import com.asempty.order_service.dto.OrderRequest;
import com.asempty.order_service.dto.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetail(long orderId);
}
