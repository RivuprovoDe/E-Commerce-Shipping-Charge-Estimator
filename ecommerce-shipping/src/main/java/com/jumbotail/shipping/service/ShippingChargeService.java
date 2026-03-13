package com.jumbotail.shipping.service;

import com.jumbotail.shipping.dto.*;
import com.jumbotail.shipping.entity.Customer;
import com.jumbotail.shipping.entity.DeliverySpeed;
import com.jumbotail.shipping.entity.Product;
import com.jumbotail.shipping.entity.TransportMode;
import com.jumbotail.shipping.entity.Warehouse;
import com.jumbotail.shipping.exception.InvalidRequestException;
import com.jumbotail.shipping.exception.ResourceNotFoundException;
import com.jumbotail.shipping.repository.CustomerRepository;
import com.jumbotail.shipping.repository.ProductRepository;
import com.jumbotail.shipping.repository.SellerRepository;
import com.jumbotail.shipping.repository.WarehouseRepository;
import com.jumbotail.shipping.util.DistanceCalculator;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ShippingChargeService {

    private final WarehouseRepository warehouseRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final WarehouseService warehouseService;
    private final ShippingStrategyFactory strategyFactory;

    // default weight used when no product is provided (e.g. direct warehouse->customer call)
    private static final double DEFAULT_WEIGHT_KG = 1.0;

    public ShippingChargeService(WarehouseRepository warehouseRepository,
                                 CustomerRepository customerRepository,
                                 ProductRepository productRepository,
                                 SellerRepository sellerRepository,
                                 WarehouseService warehouseService,
                                 ShippingStrategyFactory strategyFactory) {
        this.warehouseRepository = warehouseRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.sellerRepository = sellerRepository;
        this.warehouseService = warehouseService;
        this.strategyFactory = strategyFactory;
    }

    @Cacheable(value = "shippingCharge",
               key = "#warehouseId + '-' + #customerId + '-' + #deliverySpeed + '-' + #productId")
    public ShippingChargeResponse calculateShippingCharge(Long warehouseId, String customerId,
                                                           String deliverySpeed, String productId) {
        Warehouse warehouse = warehouseRepository.findByIdAndOperationalTrue(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not found with ID: " + warehouseId));

        Customer customer = customerRepository.findByCustomerIdAndActiveTrue(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));

        DeliverySpeed speed = DeliverySpeed.fromValue(deliverySpeed);

        double chargeableWeightKg = DEFAULT_WEIGHT_KG;
        if (productId != null && !productId.isBlank()) {
            Product product = productRepository.findByProductIdAndActiveTrue(productId)
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
            chargeableWeightKg = product.getChargeableWeightKg();
        }

        return computeCharge(warehouse, customer, speed, chargeableWeightKg);
    }

    public CalculateShippingResponse calculateSellerToCustomerShipping(CalculateShippingRequest request) {
        sellerRepository.findBySellerIdAndActiveTrue(request.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with ID: " + request.getSellerId()));

        Customer customer = customerRepository.findByCustomerIdAndActiveTrue(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + request.getCustomerId()));

        DeliverySpeed speed = DeliverySpeed.fromValue(request.getDeliverySpeed());

        double chargeableWeightKg = DEFAULT_WEIGHT_KG;
        int quantity = (request.getQuantity() != null && request.getQuantity() > 0) ? request.getQuantity() : 1;
        if (request.getProductId() != null && !request.getProductId().isBlank()) {
            Product product = productRepository.findByProductIdAndActiveTrue(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + request.getProductId()));
            chargeableWeightKg = product.getChargeableWeightKg() * quantity;
        }

        NearestWarehouseResponse nearestWarehouse = warehouseService.getNearestWarehouse(request.getSellerId());

        Warehouse warehouse = warehouseRepository.findByIdAndOperationalTrue(nearestWarehouse.getWarehouseId())
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse not available."));

        ShippingChargeResponse chargeResponse = computeCharge(warehouse, customer, speed, chargeableWeightKg);

        return CalculateShippingResponse.builder()
                .shippingCharge(chargeResponse.getShippingCharge())
                .nearestWarehouse(nearestWarehouse)
                .transportMode(chargeResponse.getTransportMode())
                .distanceKm(chargeResponse.getDistanceKm())
                .deliverySpeed(speed.getValue())
                .baseCourierCharge(chargeResponse.getBaseCourierCharge())
                .transportCharge(chargeResponse.getTransportCharge())
                .expressCharge(chargeResponse.getExpressCharge())
                .build();
    }

    private ShippingChargeResponse computeCharge(Warehouse warehouse, Customer customer,
                                                  DeliverySpeed speed, double chargeableWeightKg) {
        if (chargeableWeightKg <= 0) {
            throw new InvalidRequestException("Chargeable weight must be positive.");
        }

        double distanceKm = DistanceCalculator.calculateDistanceKm(
                warehouse.getLatitude(), warehouse.getLongitude(),
                customer.getLatitude(), customer.getLongitude());

        TransportMode transportMode = TransportMode.fromDistance(distanceKm);

        // charge = distance * rate * weight
        double transportCharge = distanceKm * transportMode.getRatePerKmPerKg() * chargeableWeightKg;

        ShippingChargeStrategy strategy = strategyFactory.getStrategy(speed);
        double expressCharge = strategy.getExpressCharge(chargeableWeightKg);
        double totalCharge = strategy.calculateTotalCharge(transportCharge, chargeableWeightKg);

        return ShippingChargeResponse.builder()
                .shippingCharge(Math.round(totalCharge * 100.0) / 100.0)
                .transportMode(transportMode.name())
                .distanceKm(Math.round(distanceKm * 100.0) / 100.0)
                .deliverySpeed(speed.getValue())
                .baseCourierCharge(DeliverySpeed.STANDARD_COURIER_CHARGE)
                .transportCharge(Math.round(transportCharge * 100.0) / 100.0)
                .expressCharge(Math.round(expressCharge * 100.0) / 100.0)
                .build();
    }
}
