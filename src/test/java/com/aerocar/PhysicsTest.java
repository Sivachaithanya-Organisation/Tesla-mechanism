package com.aerocar;

import static org.junit.jupiter.api.Assertions.*;

import com.aerocar.model.Models.*;
import com.aerocar.service.*;
import org.junit.jupiter.api.Test;

class PhysicsTest {
    static ThrusterReq demo() { return new ThrusterReq(1800, 150, 500, 300, 10, 8, 1.013); }

    @Test void demoLiftsOffAndConservesAir() {
        ThrusterRes r = ThrusterService.simulate(demo());
        assertTrue(r.canLiftOff());
        assertTrue(r.maxAltitudeM() > 1);
        assertTrue(r.airUsedKg() <= r.airMassKg() + 0.01);
    }

    @Test void weakThrusterStaysOnGround() {
        ThrusterRes r = ThrusterService.simulate(new ThrusterReq(1800, 150, 20, 300, 2, 5, 1.013));
        assertFalse(r.canLiftOff());
        assertEquals(0.0, r.maxAltitudeM(), 0.001);
    }

    @Test void rocketEquationMatchesClosedForm() {
        RocketRes r = RocketService.compare(new RocketReq(1000, 3000));
        RocketRow kerolox = r.rows().stream().filter(x -> x.propulsion().startsWith("Kerolox")).findFirst().orElseThrow();
        double expected = Math.exp(3000 / (350 * Physics.G0));
        assertEquals(expected, kerolox.massRatio(), 0.01);
    }

    @Test void jetHoverCostsMoreThanRotor() {
        HoverRes r = HoverService.compare(new HoverReq(1800, 10, 0.7, 700, 60, 200));
        assertTrue(r.powerRatio() > 1);
    }

    @Test void rejectsBadInput() {
        assertThrows(IllegalArgumentException.class, () -> RocketService.compare(new RocketReq(0, 100)));
    }
}
