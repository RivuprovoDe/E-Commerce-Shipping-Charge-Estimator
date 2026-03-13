package com.jumbotail.shipping.service;

import com.jumbotail.shipping.entity.DeliverySpeed;
import org.springframework.stereotype.Component;

// Express: Rs 10 base + Rs 1.2/kg express surcharge + transport charge
@Component
public class ExpressShippingStrategy implements ShippingChargeStrategy {

    @Override
    public double calculateTotalCharge(double transportChargeRs, double weightKg) {
        return DeliverySpeed.STANDARD_COURIER_CHARGE
                + getExpressCharge(weightKg)
                + transportChargeRs;
    }

    @Override
    public DeliverySpeed getDeliverySpeed() {
        return DeliverySpeed.EXPRESS;
    }

    @Override
    public double getExpressCharge(double weightKg) {
        return DeliverySpeed.EXPRESS_CHARGE_PER_KG * weightKg;
    }
}
