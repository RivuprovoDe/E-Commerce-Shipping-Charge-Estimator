package com.jumbotail.shipping.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearestWarehouseResponse {
    private Long warehouseId;
    private String warehouseCode;
    private String warehouseName;
    private LocationDto warehouseLocation;
    private Double distanceFromSellerKm;
}
