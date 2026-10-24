package com.aerocar.service;

import static com.aerocar.service.Physics.*;

import com.aerocar.model.Models.*;
import java.util.ArrayList;
import java.util.List;

/** Tsiolkovsky rocket equation across propulsion types (typical vacuum Isp values). */
public final class RocketService {
    private RocketService() {}

    private record Engine(String name, double isp, String example) {}

    public static RocketRes compare(RocketReq q) {
        Check.range("dryMassKg", q.dryMassKg(), 10, 1_000_000);
        Check.range("deltaVMs", q.deltaVMs(), 1, 20_000);

        double coldVe = 0.85 * Math.sqrt(2 * GAMMA_AIR / (GAMMA_AIR - 1) * R_AIR * 300); // realistic ~85% of ideal
        List<Engine> engines = List.of(
                new Engine("Cold gas (air, 300 K)", coldVe / G0, "Tesla Roadster thrusters, attitude jets"),
                new Engine("Monopropellant (hydrazine)", 230, "Satellite thrusters"),
                new Engine("Bipropellant (hypergolic)", 320, "Apollo service module, Dragon Draco"),
                new Engine("Kerolox (RP-1/LOX)", 350, "Merlin Vacuum"),
                new Engine("Methalox (CH4/LOX)", 380, "Raptor Vacuum"),
                new Engine("Hydrolox (LH2/LOX)", 450, "RL10, Centaur"),
                new Engine("Ion (xenon)", 3000, "Deep-space probes"));

        List<RocketRow> rows = new ArrayList<>();
        for (Engine e : engines) {
            double ve = e.isp() * G0;
            double mr = Math.min(Math.exp(q.deltaVMs() / ve), 1e12);
            double prop = q.dryMassKg() * (mr - 1);
            rows.add(new RocketRow(e.name(), r2(e.isp()), r2(ve), r2(mr), r2(prop), r2((1 - 1 / mr) * 100),
                    mr <= 10, e.example()));
        }
        double hop = q.deltaVMs() * q.deltaVMs() / (2 * G0);
        List<String> n = new ArrayList<>();
        n.add("Equation: dv = Isp x g0 x ln(m0/mf). Exhaust velocity sits in the exponent, so small Isp gains cut propellant massively.");
        n.add("Mass ratio above ~10 is marked impractical for a single stage (structure and engines need mass too).");
        n.add("Orbit needs roughly 9.4 km/s including gravity and drag losses. Cold gas at ~" + r2(coldVe / G0)
                + " s Isp makes that impossible: it fits hops and attitude control, not flight to orbit.");
        n.add("Ion drives win on Isp but give newtons of thrust, so they only work in space, never for lifting off a planet.");
        n.add("Ignoring losses, this dv equals a ballistic hop of " + r2(hop) + " m on Earth.");
        return new RocketRes(rows, r2(hop), n);
    }
}
