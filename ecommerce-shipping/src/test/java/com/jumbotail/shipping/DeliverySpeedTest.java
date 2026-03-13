package com.jumbotail.shipping;

import com.jumbotail.shipping.entity.DeliverySpeed;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeliverySpeedTest {

    @Test
    void testFromValueStandard() {
        assertEquals(DeliverySpeed.STANDARD, DeliverySpeed.fromValue("standard"));
        assertEquals(DeliverySpeed.STANDARD, DeliverySpeed.fromValue("STANDARD"));
        assertEquals(DeliverySpeed.STANDARD, DeliverySpeed.fromValue("Standard"));
    }

    @Test
    void testFromValueExpress() {
        assertEquals(DeliverySpeed.EXPRESS, DeliverySpeed.fromValue("express"));
        assertEquals(DeliverySpeed.EXPRESS, DeliverySpeed.fromValue("EXPRESS"));
    }

    @Test
    void testFromValueInvalid() {
        assertThrows(IllegalArgumentException.class, () -> DeliverySpeed.fromValue("overnight"));
        assertThrows(IllegalArgumentException.class, () -> DeliverySpeed.fromValue(""));
        assertThrows(IllegalArgumentException.class, () -> DeliverySpeed.fromValue(null));
    }

    @Test
    void testConstants() {
        assertEquals(10.0, DeliverySpeed.STANDARD_COURIER_CHARGE);
        assertEquals(1.2, DeliverySpeed.EXPRESS_CHARGE_PER_KG);
    }
}
