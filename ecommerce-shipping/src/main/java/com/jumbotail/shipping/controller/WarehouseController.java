package com.jumbotail.shipping.controller;

import com.jumbotail.shipping.dto.NearestWarehouseResponse;
import com.jumbotail.shipping.exception.InvalidRequestException;
import com.jumbotail.shipping.exception.ResourceNotFoundException;
import com.jumbotail.shipping.repository.ProductRepository;
import com.jumbotail.shipping.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final ProductRepository productRepository;

    public WarehouseController(WarehouseService warehouseService,
                               ProductRepository productRepository) {
        this.warehouseService = warehouseService;
        this.productRepository = productRepository;
    }

    @GetMapping("/nearest")
    public ResponseEntity<NearestWarehouseResponse> getNearestWarehouse(
            @RequestParam String sellerId,
            @RequestParam String productId) {

        if (sellerId == null || sellerId.isBlank()) {
            throw new InvalidRequestException("sellerId cannot be empty");
        }
        if (productId == null || productId.isBlank()) {
            throw new InvalidRequestException("productId cannot be empty");
        }

        // validate the product exists even though routing only uses seller location
        productRepository.findByProductIdAndActiveTrue(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));

        NearestWarehouseResponse response = warehouseService.getNearestWarehouse(sellerId);
        return ResponseEntity.ok(response);
    }
}
