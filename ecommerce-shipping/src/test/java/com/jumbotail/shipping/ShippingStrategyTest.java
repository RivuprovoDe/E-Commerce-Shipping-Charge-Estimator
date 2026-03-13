package com.jumbotail.shipping;

import com.jumbotail.shipping.entity.DeliverySpeed;
import com.jumbotail.shipping.service.ExpressShippingStrategy;
import com.jumbotail.shipping.service.StandardShippingStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShippingStrategyTest {

    private final StandardShippingStrategy standardStrategy = new StandardShippingStrategy();
    private final ExpressShippingStrategy expressStrategy = new ExpressShippingStrategy();

    @Test
    void testStandardStrategy_TotalCharge() {
        // total = 10 (base) + 100 (transport) = 110
        double total = standardStrategy.calculateTotalCharge(100.0, 5.0);
        assertEquals(110.0, total, 0.001);
    }

    @Test
    void testStandardStrategy_NoExpressCharge() {
        assertEquals(0.0, standardStrategy.getExpressCharge(10.0));
    }

    @Test
    void testStandardStrategy_DeliverySpeed() {
        assertEquals(DeliverySpeed.STANDARD, standardStrategy.getDeliverySpeed());
    }

    @Test
    void testExpressStrategy_TotalCharge() {
        // total = 10 (base) + (1.2 * 5 = 6) (express) + 100 (transport) = 116
        double total = expressStrategy.calculateTotalCharge(100.0, 5.0);
        assertEquals(116.0, total, 0.001);
    }

    @Test
    void testExpressStrategy_ExpressCharge() {
        // express charge = 1.2 * 10 = 12
        assertEquals(12.0, expressStrategy.getExpressCharge(10.0), 0.001);
    }

    @Test
    void testExpressStrategy_DeliverySpeed() {
        assertEquals(DeliverySpeed.EXPRESS, expressStrategy.getDeliverySpeed());
    }
}
