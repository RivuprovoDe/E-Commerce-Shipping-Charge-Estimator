package com.jumbotail.shipping.entity;

public enum DeliverySpeed {

    STANDARD("standard"),
    EXPRESS("express");

    private final String value;

    public static final double STANDARD_COURIER_CHARGE = 10.0;
    public static final double EXPRESS_CHARGE_PER_KG = 1.2;

    DeliverySpeed(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DeliverySpeed fromValue(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Delivery speed cannot be null");
        }
        for (DeliverySpeed speed : values()) {
            if (speed.value.equalsIgnoreCase(value)) {
                return speed;
            }
        }
        throw new IllegalArgumentException("Invalid delivery speed: " + value + ". Must be 'standard' or 'express'");
    }
}
