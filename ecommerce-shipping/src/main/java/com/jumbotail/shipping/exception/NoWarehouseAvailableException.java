package com.jumbotail.shipping.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class NoWarehouseAvailableException extends RuntimeException {
    public NoWarehouseAvailableException(String message) {
        super(message);
    }
}
