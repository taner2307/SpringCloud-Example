package com.asempty.order_service.client;

import com.asempty.order_service.dto.ProductResponse;
import com.asempty.order_service.exception.CustomException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PRODUCT-SERVICE/product")
@CircuitBreaker(name = "external", fallbackMethod = "fallback")
public interface ProductServiceClient {

    @PutMapping("/reduce/{id}")
     ResponseEntity<Void> reduceQuantity(@PathVariable("id") long productId, @RequestParam long quantity);

    @GetMapping("/{id}")
    ResponseEntity<ProductResponse> getProductById(@PathVariable("id") long productId);

    default void fallback(Exception e) {
        throw new CustomException("Product Service is unavailable", "UNAVAILABLE", 500);
    }
}
