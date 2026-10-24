package com.aerocar.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Curated content. Tesla facts are from news coverage of the 15 Oct 2026 event (CNN/ABC, AFP, Business Today,
 * CBS via Northeast Times). Market statuses change quickly: review before each release.
 */
public final class ContentService {
    private ContentService() {}
    static final String AS_OF = "2026-10-24";

    public static Map<String, Object> tesla() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("asOf", AS_OF);
        m.put("headline", "Tesla unveiled its next-generation Roadster on 15 October 2026 at SpaceX's McGregor, Texas test site. "
                + "It is a thruster-assisted sports car, not yet a released product: production is targeted for 2028.");
        m.put("confirmed", List.of(
                "The demo car was unmanned (Musk said this was so nobody gets hurt).",
                "It climbed a steeply curved ramp, hung upside down, left the end, rolled in mid-air and landed on its wheels.",
                "Reports describe about 10 nozzles fed by ultra high-pressure air tanks, plus a fan/suction system under the car.",
                "SpaceX-developed cold-gas thrusters: compressed gas pushed through nozzles, with no combustion.",
                "A claimed 0 to 60 mph in under 1 second, helped by thrust on the ground.",
                "Three variants listed for reservations: Performance, Thrust and Rocket; the upper two are reported to get the thruster package.",
                "Tesla reopened reservations; Musk targets 2028 production."));
        m.put("caveats", List.of(
                "What was shown is a ballistic, ramp-assisted jump with thruster control. It is not sustained, free flight or hover.",
                "Musk himself said it was not necessarily safety-optimised and may not be legal everywhere.",
                "Analysts quoted by CNN doubt a street-legal car could fly anywhere its owner wants.",
                "Tesla's Roadster demo date slipped several times (late 2025 to Oct 2026), so 2028 is a target, not a guarantee.",
                "Exact tank pressure, mass, nozzle sizes and range are not public. The simulator uses illustrative assumptions."));
        m.put("mechanism", List.of(
                Map.of("title", "1. Store energy as pressure", "text", "Air is compressed into high-pressure tanks. The energy is the work done compressing it."),
                Map.of("title", "2. Vent through nozzles", "text", "Opening a valve lets air accelerate through a converging-diverging nozzle. At high pressure ratios the throat chokes at Mach 1, so mass flow depends on pressure and area."),
                Map.of("title", "3. Newton's third law", "text", "Thrust = mass flow x exhaust velocity (plus a pressure term). The car accelerates opposite to the jet."),
                Map.of("title", "4. Steer with several jets", "text", "Thrusters at different positions create torque for flips and attitude control, the same idea as spacecraft reaction-control systems."),
                Map.of("title", "5. Fans press it to the surface", "text", "A fan under the car lowers the pressure beneath it, giving downforce so it can drive up a steep ramp, even inverted."),
                Map.of("title", "6. Blow-down limits endurance", "text", "Tank pressure and thrust fall as air is used. That limits cold gas to seconds of thrust; try it in the simulator.")));
        m.put("timeline", List.of(
                Map.of("date", "2017", "text", "Next-generation Roadster first announced."),
                Map.of("date", "2025 - 2026", "text", "Musk teases a 'SpaceX package' and repeatedly moves the demo date."),
                Map.of("date", "28 Sep 2026", "text", "Event postponed due to weather forecast; rescheduled for 15 Oct."),
                Map.of("date", "15 Oct 2026", "text", "Livestreamed demo in McGregor, Texas; reservations reopened."),
                Map.of("date", "2028", "text", "Stated production target.")));
        m.put("sources", List.of(
                Map.of("title", "CNN via ABC30: Musk unveils the Roadster", "url", "https://abc30.com/story/can-new-tesla-roadster-fly-elon-musk-unveils-what-he-says-is-flying-car/19944143/"),
                Map.of("title", "AFP via Citizen Digital", "url", "https://citizen.digital/article/elon-musk-unveils-teslas-flying-car-n392236"),
                Map.of("title", "Business Today: cold-gas thrusters", "url", "https://www.businesstoday.in/auto/story/is-tesla-bringing-flying-cars-elon-musks-teaser-video-clip-triggers-buzz-watch-561960-2026-10-16"),
                Map.of("title", "RTTNews: variants and reservations", "url", "https://www.rttnews.com/3699759/tesla-unveils-new-roadster-the-flying-car.aspx")));
        return m;
    }

    public static Map<String, Object> market() {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("asOf", AS_OF);
        m.put("note", "Statuses move fast and several figures below come from press coverage. Verify with the company or regulator before decisions. "
                + "Market-size forecasts differ enormously between research firms (a few billion to tens of billions USD for 2026), so treat them as rough.");
        m.put("players", List.of(
                Map.of("name", "Tesla Roadster", "category", "Thruster-assisted car", "lift", "Cold-gas thrusters + fans", "status", "Unveiled 15 Oct 2026; production targeted 2028; unmanned ramp-assisted demo, not sustained flight."),
                Map.of("name", "Joby Aviation", "category", "eVTOL air taxi", "lift", "Tilting electric propellers", "status", "Most advanced US type-certification effort; reported flying under the US eVTOL Integration Pilot Program; Dubai targeted early."),
                Map.of("name", "Archer Aviation", "category", "eVTOL air taxi", "lift", "Electric rotors (Midnight)", "status", "Pursuing FAA certification; reported selected for the eIPP; UAE and Florida plans."),
                Map.of("name", "EHang", "category", "Autonomous eVTOL", "lift", "Electric multicopter", "status", "Operates under Chinese regulatory approvals; check current passenger-service status."),
                Map.of("name", "Alef Aeronautics", "category", "Roadable flying car", "lift", "Electric lift system, drives on roads", "status", "Early-stage; FAA special airworthiness certificate reported in 2025; pre-orders."),
                Map.of("name", "Jetson ONE", "category", "Personal eVTOL", "lift", "Eight electric rotors", "status", "Customer deliveries reported to have begun in March 2026."),
                Map.of("name", "XPeng AeroHT", "category", "Modular car + flying module", "lift", "Electric rotors on a separate module", "status", "Targeting production; verify current timeline.")));
        m.put("compare", List.of(
                Map.of("trait", "Lift principle", "tesla", "Jet reaction from stored gas", "evtol", "Rotor momentum from batteries"),
                Map.of("trait", "Hover power", "tesla", "Very high (fast small jet)", "evtol", "Low (large slow disc)"),
                Map.of("trait", "Endurance", "tesla", "Seconds (tank blow-down)", "evtol", "Tens of minutes"),
                Map.of("trait", "Noise", "tesla", "Extremely loud", "evtol", "Quieter by design"),
                Map.of("trait", "Regulation", "tesla", "Road vehicle with stunt mode", "evtol", "Aircraft certification (FAA/EASA/CAAC)"),
                Map.of("trait", "Best use", "tesla", "Performance, jumps, control", "evtol", "City and regional transport")));
        return m;
    }
}
