package com.aerocar.service;

import static com.aerocar.service.Physics.*;

import com.aerocar.model.Models.*;
import java.util.ArrayList;
import java.util.List;

/** Momentum theory: why big slow rotors hover cheaply and small fast jets are power-hungry. */
public final class HoverService {
    private HoverService() {}
    private static final double RHO = 1.225; // sea-level air density, kg/m3

    public static HoverRes compare(HoverReq q) {
        Check.range("massKg", q.massKg(), 50, 50_000);
        Check.range("rotorDiscAreaM2", q.rotorDiscAreaM2(), 0.1, 500);
        Check.range("figureOfMerit", q.figureOfMerit(), 0.3, 0.95);
        Check.range("jetExhaustVelocityMs", q.jetExhaustVelocityMs(), 50, 5_000);
        Check.range("hoverSeconds", q.hoverSeconds(), 1, 3_600);
        Check.range("batteryWhPerKg", q.batteryWhPerKg(), 50, 500);

        double W = q.massKg() * G0;
        double ideal = Math.pow(W, 1.5) / Math.sqrt(2 * RHO * q.rotorDiscAreaM2());
        double rotor = ideal / q.figureOfMerit();
        double jet = 0.5 * W * q.jetExhaustVelocityMs();
        double kWh = rotor * q.hoverSeconds() / 3.6e6;
        double battery = kWh * 1000 / q.batteryWhPerKg();
        double propellant = W / q.jetExhaustVelocityMs() * q.hoverSeconds();

        List<String> n = new ArrayList<>();
        n.add("A rotor moves lots of air slowly, so thrust costs little power. A jet moves little air fast, so most power ends up as wasted exhaust kinetic energy.");
        n.add("Jet hover needs about " + r2(jet / rotor) + "x the power of the rotor, and it must carry its air: "
                + r2(propellant) + " kg for " + (int) q.hoverSeconds() + " s of hover.");
        n.add("This is why eVTOLs (Joby, Archer, EHang) use large rotors and Tesla's thrusters are used for short bursts.");
        return new HoverRes(r2(W / 1000), r2(W / q.rotorDiscAreaM2()), r2(rotor / 1000), r2(jet / 1000),
                r2(jet / rotor), r2(kWh), r2(battery), r2(propellant), n);
    }
}
