package com.aerocar.service;

import static com.aerocar.service.Physics.*;

import com.aerocar.model.Models.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Cold-gas thruster blow-down: compressed air in a tank vents through choked/subsonic nozzles.
 * Tank gas expands isentropically (p ~ rho^gamma). Thrust = efficiency * mdot * ve.
 * Vertical motion only, no aerodynamic drag. Educational model, NOT Tesla's actual specification.
 */
public final class ThrusterService {
    private ThrusterService() {}
    private static final double CD = 0.95;        // nozzle discharge coefficient
    private static final double NOZZLE_EFF = 0.92; // thrust efficiency vs ideal expansion

    public static ThrusterRes simulate(ThrusterReq q) {
        Check.range("dryMassKg", q.dryMassKg(), 100, 20_000);
        Check.range("tankVolumeL", q.tankVolumeL(), 1, 3_000);
        Check.range("tankPressureBar", q.tankPressureBar(), 2, 1_000);
        Check.range("tankTempK", q.tankTempK(), 150, 600);
        Check.range("nozzleCount", q.nozzleCount(), 1, 50);
        Check.range("throatDiameterMm", q.throatDiameterMm(), 1, 100);
        Check.range("ambientPressureBar", q.ambientPressureBar(), 0, 1.1);

        final double g = GAMMA_AIR, R = R_AIR;
        double V = q.tankVolumeL() / 1000.0, p0 = q.tankPressureBar() * 1e5, T0 = q.tankTempK();
        double pa = q.ambientPressureBar() * 1e5;
        double rho0 = p0 / (R * T0), M0 = rho0 * V, M = M0;
        double At = q.nozzleCount() * Math.PI * Math.pow(q.throatDiameterMm() / 1000.0, 2) / 4 * CD;
        double crit = Math.pow(2 / (g + 1), g / (g - 1));

        double dt = 0.0005, t = 0, h = 0, v = 0, maxH = 0, impulse = 0;
        double f0 = 0, ve0 = 0, mdot0 = 0, liftT = 0, firstAir = -1;
        boolean airborne = false;
        List<ThrusterPoint> series = new ArrayList<>();

        for (long step = 0; t < 90; step++) {
            double rho = M / V;
            double p = p0 * Math.pow(rho / rho0, g);
            double T = p / (rho * R);
            double F = 0, mdot = 0;
            if (M > 1e-6 && p > pa * 1.02) {
                double ratio = pa / p;
                mdot = ratio <= crit
                        ? At * p * Math.sqrt(g / (R * T)) * Math.pow(2 / (g + 1), (g + 1) / (2 * (g - 1)))
                        : At * p * Math.sqrt(2 * g / ((g - 1) * R * T)
                                * (Math.pow(ratio, 2 / g) - Math.pow(ratio, (g + 1) / g)));
                mdot = Math.min(mdot, M / dt);
                double ve = exhaustVelocity(p, T, pa, g, R);
                F = NOZZLE_EFF * mdot * ve;
                if (step == 0) { f0 = F; ve0 = NOZZLE_EFF * ve; mdot0 = mdot; }
            }
            double m = q.dryMassKg() + M;
            if (F > m * G0) liftT += dt;
            double a = F / m - G0;
            if (!airborne && h <= 0 && a < 0) { a = 0; v = 0; }
            v += a * dt;
            h += v * dt;
            if (h > 0.01 && !airborne) { airborne = true; firstAir = t; }
            if (airborne && h <= 0) { h = 0; t += dt; break; }
            maxH = Math.max(maxH, h);
            impulse += F * dt;
            M -= mdot * dt;
            if (step % 200 == 0 && series.size() < 700) {
                series.add(new ThrusterPoint(r2(t), r2(F / 1000), r2(h), r2(p / 1e5)));
            }
            if (!airborne && F == 0) break;           // never lifted and tank is spent
            t += dt;
        }

        double weight0 = (q.dryMassKg() + M0) * G0;
        double tw = f0 / weight0;
        double airTime = airborne ? t - firstAir : 0;
        List<String> notes = new ArrayList<>();
        notes.add(tw > 1 ? "Initial thrust-to-weight is " + r2(tw) + ": the thrusters can lift this mass."
                : "Thrust-to-weight is only " + r2(tw) + " (< 1). It cannot leave the ground, so raise pressure, nozzle area or cut mass.");
        notes.add("Stored air is only " + r2(M0) + " kg and it vents fast because tank pressure falls as gas leaves. "
                + "Useful thrust lasts " + r2(liftT) + " s, so cold gas gives a hop, not sustained flight.");
        notes.add("Isp is about " + r2(ve0 / G0) + " s. Chemical rockets reach 280 to 450 s, so this air is far less efficient per kilogram.");
        return new ThrusterRes(r2(M0), r2(M0 - M), r2(f0 / 1000), r2(tw), tw > 1, r2(ve0 / G0), r2(liftT), r2(maxH),
                r2(airTime), r2(impulse / 1000), notes, series);
    }
}
