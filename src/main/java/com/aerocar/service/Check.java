package com.aerocar.service;

final class Check {
    private Check() {}
    static void range(String name, double v, double lo, double hi) {
        if (Double.isNaN(v) || v < lo || v > hi) {
            throw new IllegalArgumentException(name + " must be between " + lo + " and " + hi);
        }
    }
}
