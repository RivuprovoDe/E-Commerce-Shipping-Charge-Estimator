package com.jumbotail.shipping.service;

import com.jumbotail.shipping.entity.DeliverySpeed;
import com.jumbotail.shipping.exception.InvalidRequestException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// Picks the right strategy based on delivery speed.
// Spring auto-injects all ShippingChargeStrategy beans so adding a new speed
// only requires a new strategy class, nothing changes here.
@Component
public class ShippingStrategyFactory {

    private final Map<DeliverySpeed, ShippingChargeStrategy> strategies;

    public ShippingStrategyFactory(List<ShippingChargeStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(ShippingChargeStrategy::getDeliverySpeed, Function.identity()));
    }

    public ShippingChargeStrategy getStrategy(DeliverySpeed speed) {
        ShippingChargeStrategy strategy = strategies.get(speed);
        if (strategy == null) {
            throw new InvalidRequestException("No shipping strategy found for delivery speed: " + speed);
        }
        return strategy;
    }
}
