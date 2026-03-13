package com.jumbotail.shipping.controller;

import com.jumbotail.shipping.dto.CalculateShippingRequest;
import com.jumbotail.shipping.dto.CalculateShippingResponse;
import com.jumbotail.shipping.dto.ShippingChargeResponse;
import com.jumbotail.shipping.exception.InvalidRequestException;
import com.jumbotail.shipping.service.ShippingChargeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipping-charge")
public class ShippingController {

    private final ShippingChargeService shippingChargeService;

    public ShippingController(ShippingChargeService shippingChargeService) {
        this.shippingChargeService = shippingChargeService;
    }

    @GetMapping
    public ResponseEntity<ShippingChargeResponse> getShippingCharge(
            @RequestParam Long warehouseId,
            @RequestParam String customerId,
            @RequestParam String deliverySpeed,
            @RequestParam(required = false) String productId) {

        if (warehouseId == null || warehouseId <= 0) {
            throw new InvalidRequestException("warehouseId must be a positive number");
        }
        if (customerId == null || customerId.isBlank()) {
            throw new InvalidRequestException("customerId cannot be empty");
        }
        if (deliverySpeed == null || deliverySpeed.isBlank()) {
            throw new InvalidRequestException("deliverySpeed cannot be empty");
        }

        ShippingChargeResponse response = shippingChargeService
                .calculateShippingCharge(warehouseId, customerId, deliverySpeed, productId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/calculate")
    public ResponseEntity<CalculateShippingResponse> calculateShipping(
            @Valid @RequestBody CalculateShippingRequest request) {

        CalculateShippingResponse response = shippingChargeService.calculateSellerToCustomerShipping(request);
        return ResponseEntity.ok(response);
    }
}
