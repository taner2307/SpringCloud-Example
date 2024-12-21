package com.asempty.ProductService.service;

import com.asempty.ProductService.dto.ProductRequest;
import com.asempty.ProductService.dto.ProductResponse;
import com.asempty.ProductService.entity.Product;
import com.asempty.ProductService.exception.ProductServiceException;
import com.asempty.ProductService.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@RequiredArgsConstructor
@Log4j2
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding product {}", productRequest);

        Product product = Product.builder()
                .productName(productRequest.getProductName())
                .price(productRequest.getPrice())
                .quantity(productRequest.getQuantity())
                .build();
        productRepository.save(product);
        log.info("Product added");
        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductServiceException("PRODUCT_NOT_FOUND", "Product not found"));

        ProductResponse productResponse = new ProductResponse();
        copyProperties(product, productResponse);
        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Quantity: {} for Id: {}", quantity ,productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductServiceException("Prduct not found","PRODUCT_NOT_FOUND"));
        if(product.getQuantity() < quantity) {
            throw new ProductServiceException("Product does not have sufficient Quantity", "INSUFFICIENT_QUANTITY");
        }
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);
        log.info("Product quantity updated");
    }
}
