package com.jumbotail.shipping.service;

import com.jumbotail.shipping.entity.DeliverySpeed;
import org.springframework.stereotype.Component;

// Standard: Rs 10 base + transport charge
@Component
public class StandardShippingStrategy implements ShippingChargeStrategy {

    @Override
    public double calculateTotalCharge(double transportChargeRs, double weightKg) {
        return DeliverySpeed.STANDARD_COURIER_CHARGE + transportChargeRs;
    }

    @Override
    public DeliverySpeed getDeliverySpeed() {
        return DeliverySpeed.STANDARD;
    }

    @Override
    public double getExpressCharge(double weightKg) {
        return 0.0;
    }
}
