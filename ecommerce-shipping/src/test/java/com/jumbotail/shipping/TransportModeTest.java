package com.jumbotail.shipping;

import com.jumbotail.shipping.entity.TransportMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransportModeTest {

    @Test
    void testMiniVanForShortDistance() {
        assertEquals(TransportMode.MINI_VAN, TransportMode.fromDistance(50.0));
        assertEquals(TransportMode.MINI_VAN, TransportMode.fromDistance(0.0));
        assertEquals(TransportMode.MINI_VAN, TransportMode.fromDistance(99.9));
    }

    @Test
    void testTruckForMediumDistance() {
        assertEquals(TransportMode.TRUCK, TransportMode.fromDistance(100.0));
        assertEquals(TransportMode.TRUCK, TransportMode.fromDistance(300.0));
        assertEquals(TransportMode.TRUCK, TransportMode.fromDistance(499.9));
    }

    @Test
    void testAeroplaneForLongDistance() {
        assertEquals(TransportMode.AEROPLANE, TransportMode.fromDistance(500.0));
        assertEquals(TransportMode.AEROPLANE, TransportMode.fromDistance(1000.0));
        assertEquals(TransportMode.AEROPLANE, TransportMode.fromDistance(5000.0));
    }

    @Test
    void testRates() {
        assertEquals(3.0, TransportMode.MINI_VAN.getRatePerKmPerKg());
        assertEquals(2.0, TransportMode.TRUCK.getRatePerKmPerKg());
        assertEquals(1.0, TransportMode.AEROPLANE.getRatePerKmPerKg());
    }
}
