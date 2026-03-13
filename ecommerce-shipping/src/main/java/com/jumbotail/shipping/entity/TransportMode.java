package com.jumbotail.shipping.entity;

public enum TransportMode {

    AEROPLANE(500.0, Double.MAX_VALUE, 1.0),  // 500km+ -> Rs 1/km/kg
    TRUCK(100.0, 500.0, 2.0),                 // 100-500km -> Rs 2/km/kg
    MINI_VAN(0.0, 100.0, 3.0);               // 0-100km -> Rs 3/km/kg

    private final double minDistanceKm;
    private final double maxDistanceKm;
    private final double ratePerKmPerKg;

    TransportMode(double minDistanceKm, double maxDistanceKm, double ratePerKmPerKg) {
        this.minDistanceKm = minDistanceKm;
        this.maxDistanceKm = maxDistanceKm;
        this.ratePerKmPerKg = ratePerKmPerKg;
    }

    public double getRatePerKmPerKg() {
        return ratePerKmPerKg;
    }

    public static TransportMode fromDistance(double distanceKm) {
        for (TransportMode mode : values()) {
            if (distanceKm >= mode.minDistanceKm && distanceKm < mode.maxDistanceKm) {
                return mode;
            }
        }
        return AEROPLANE;
    }
}
