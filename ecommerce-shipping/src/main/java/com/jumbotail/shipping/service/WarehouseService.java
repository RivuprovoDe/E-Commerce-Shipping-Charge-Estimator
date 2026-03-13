package com.jumbotail.shipping.service;

import com.jumbotail.shipping.dto.LocationDto;
import com.jumbotail.shipping.dto.NearestWarehouseResponse;
import com.jumbotail.shipping.entity.Seller;
import com.jumbotail.shipping.entity.Warehouse;
import com.jumbotail.shipping.exception.NoWarehouseAvailableException;
import com.jumbotail.shipping.exception.ResourceNotFoundException;
import com.jumbotail.shipping.repository.SellerRepository;
import com.jumbotail.shipping.repository.WarehouseRepository;
import com.jumbotail.shipping.util.DistanceCalculator;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final SellerRepository sellerRepository;

    public WarehouseService(WarehouseRepository warehouseRepository,
                            SellerRepository sellerRepository) {
        this.warehouseRepository = warehouseRepository;
        this.sellerRepository = sellerRepository;
    }

    // Warehouse proximity only depends on the seller's location, so we cache by sellerId.
    // Product validation is the caller's responsibility (see WarehouseController).
    @Cacheable(value = "nearestWarehouse", key = "#sellerId")
    public NearestWarehouseResponse getNearestWarehouse(String sellerId) {
        Seller seller = sellerRepository.findBySellerIdAndActiveTrue(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with ID: " + sellerId));

        List<Warehouse> warehouses = warehouseRepository.findByOperationalTrue();
        if (warehouses.isEmpty()) {
            throw new NoWarehouseAvailableException("No operational warehouses are currently available.");
        }

        Warehouse nearest = warehouses.stream()
                .min(Comparator.comparingDouble(w ->
                        DistanceCalculator.calculateDistanceKm(
                                seller.getLatitude(), seller.getLongitude(),
                                w.getLatitude(), w.getLongitude())))
                .orElseThrow(() -> new NoWarehouseAvailableException("Could not determine nearest warehouse."));

        double distanceKm = DistanceCalculator.calculateDistanceKm(
                seller.getLatitude(), seller.getLongitude(),
                nearest.getLatitude(), nearest.getLongitude());

        return NearestWarehouseResponse.builder()
                .warehouseId(nearest.getId())
                .warehouseCode(nearest.getWarehouseCode())
                .warehouseName(nearest.getName())
                .warehouseLocation(LocationDto.builder()
                        .lat(nearest.getLatitude())
                        .lng(nearest.getLongitude())
                        .build())
                .distanceFromSellerKm(Math.round(distanceKm * 100.0) / 100.0)
                .build();
    }
}
