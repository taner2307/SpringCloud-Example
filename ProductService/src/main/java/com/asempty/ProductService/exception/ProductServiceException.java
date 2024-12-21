package com.asempty.ProductService.exception;

import lombok.Data;

@Data
public class ProductServiceException extends RuntimeException {

    private String errorCode;

    public ProductServiceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
