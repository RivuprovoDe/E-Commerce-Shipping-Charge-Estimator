package com.jumbotail.shipping.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DistanceCalculatorTest {

    @Test
    void testSameLocationReturnsZero() {
        double distance = DistanceCalculator.calculateDistanceKm(12.9716, 77.5946, 12.9716, 77.5946);
        assertEquals(0.0, distance, 0.001);
    }

    @Test
    void testKnownDistanceBangaloreToMumbai() {
        // Approx 845 km
        double distance = DistanceCalculator.calculateDistanceKm(12.9716, 77.5946, 19.0760, 72.8777);
        assertTrue(distance > 800 && distance < 900, "BLR to MUMB should be approx 845 km, got: " + distance);
    }

    @Test
    void testNorthSouthDistance() {
        double distance = DistanceCalculator.calculateDistanceKm(8.0, 77.0, 28.0, 77.0);
        // Approx 2200 km north-south
        assertTrue(distance > 2000, "North-south distance should be large");
    }
}
