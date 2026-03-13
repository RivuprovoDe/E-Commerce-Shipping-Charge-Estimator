package com.jumbotail.shipping.service;

import com.jumbotail.shipping.entity.DeliverySpeed;

// Strategy pattern - each delivery speed has its own charge calculation
public interface ShippingChargeStrategy {

    double calculateTotalCharge(double transportChargeRs, double weightKg);

    DeliverySpeed getDeliverySpeed();

    double getExpressCharge(double weightKg);
}
