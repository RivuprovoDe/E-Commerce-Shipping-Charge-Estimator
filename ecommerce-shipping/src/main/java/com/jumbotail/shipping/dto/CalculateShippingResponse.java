package com.jumbotail.shipping.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateShippingResponse {
    private Double shippingCharge;
    private NearestWarehouseResponse nearestWarehouse;
    private String transportMode;
    private Double distanceKm;
    private String deliverySpeed;
    private Double baseCourierCharge;
    private Double transportCharge;
    private Double expressCharge;
}
