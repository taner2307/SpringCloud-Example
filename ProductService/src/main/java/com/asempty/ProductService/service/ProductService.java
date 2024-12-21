package com.asempty.ProductService.service;

import com.asempty.ProductService.dto.ProductRequest;
import com.asempty.ProductService.dto.ProductResponse;

public interface ProductService {

    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}
