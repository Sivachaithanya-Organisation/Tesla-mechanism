package com.aerocar.model;

import java.util.List;

public final class Models {
    private Models() {}

    // Cold-gas thruster blow-down simulation
    public record ThrusterReq(double dryMassKg, double tankVolumeL, double tankPressureBar, double tankTempK,
                              int nozzleCount, double throatDiameterMm, double ambientPressureBar) {}
    public record ThrusterPoint(double t, double thrustKN, double altitudeM, double pressureBar) {}
    public record ThrusterRes(double airMassKg, double airUsedKg, double initialThrustKN, double thrustToWeight,
                              boolean canLiftOff, double ispSeconds, double liftSeconds, double maxAltitudeM,
                              double airTimeS, double totalImpulseKNs, List<String> insights,
                              List<ThrusterPoint> series) {}

    // Hover power: rotor/fan vs jet
    public record HoverReq(double massKg, double rotorDiscAreaM2, double figureOfMerit,
                           double jetExhaustVelocityMs, double hoverSeconds, double batteryWhPerKg) {}
    public record HoverRes(double weightKN, double discLoadingNPerM2, double rotorHoverPowerKW,
                           double jetHoverPowerKW, double powerRatio, double rotorEnergyKWh,
                           double batteryMassKg, double jetPropellantKg, List<String> insights) {}

    // Tsiolkovsky comparison
    public record RocketReq(double dryMassKg, double deltaVMs) {}
    public record RocketRow(String propulsion, double ispS, double exhaustVelocityMs, double massRatio,
                            double propellantKg, double propellantFractionPct, boolean practical, String example) {}
    public record RocketRes(List<RocketRow> rows, double hopHeightEquivalentM, List<String> insights) {}
}
