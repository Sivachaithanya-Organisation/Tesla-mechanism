package com.aerocar.service;

/** Gas-dynamics constants and helpers (ideal gas, isentropic flow). */
public final class Physics {
    private Physics() {}
    public static final double G0 = 9.80665;
    public static final double GAMMA_AIR = 1.4;
    public static final double R_AIR = 287.05;

    public static double r2(double v) { return Math.round(v * 100.0) / 100.0; }

    /** Isentropic exhaust velocity expanding from stagnation (p, T) to exit pressure pe. */
    public static double exhaustVelocity(double p, double T, double pe, double gamma, double R) {
        double ratio = Math.min(1.0, Math.max(0.0, pe / p));
        return Math.sqrt(2 * gamma / (gamma - 1) * R * T * (1 - Math.pow(ratio, (gamma - 1) / gamma)));
    }
}
