package com.jumbotail.shipping.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalculateShippingRequest {

    @NotBlank(message = "sellerId is required")
    private String sellerId;

    @NotBlank(message = "customerId is required")
    private String customerId;

    @NotBlank(message = "deliverySpeed is required")
    private String deliverySpeed;

    private String productId;
    private Integer quantity;
}
