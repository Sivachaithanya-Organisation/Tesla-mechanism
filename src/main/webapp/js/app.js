(() => {
  'use strict';
  const $ = (s) => document.querySelector(s);
  let current = null, chart = null;

  const esc = (s) => String(s).replace(/[&<>"']/g, (c) => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
  const n = (v, d = 1) => (Math.abs(v) >= 1e9 ? Number(v).toExponential(2) : Number(v).toLocaleString(undefined, { maximumFractionDigits: d }));
  const kv = (k, v, cls = '') => `<div class="kv ${cls}"><span>${k}</span><strong>${v}</strong></div>`;
  const ul = (a) => `<ul class="tips">${a.map((t) => `<li>${esc(t)}</li>`).join('')}</ul>`;
  const badge = (t, ok) => `<span class="badge ${ok ? 'good' : 'warn'}">${esc(t)}</span>`;
  const table = (head, rows) => `<table><tr>${head.map((h) => `<th>${h}</th>`).join('')}</tr>${rows.map((r) => `<tr>${r.map((c) => `<td>${c}</td>`).join('')}</tr>`).join('')}</table>`;

  const MODULES = [
    { key: 'tesla', title: 'Tesla Roadster overview', get: true,
      blurb: 'What was actually shown on 15 Oct 2026, how the mechanism works, and what is still unproven.',
      render: (r) => `<p>${esc(r.headline)}</p><h3>What was shown</h3>${ul(r.confirmed)}<h3>Caveats</h3>${ul(r.caveats)}`
        + `<h3>How the mechanism works</h3>${r.mechanism.map((s) => `<div class="step"><b>${esc(s.title)}</b>${esc(s.text)}</div>`).join('')}`
        + `<h3>Timeline</h3>${table(['When', 'Event'], r.timeline.map((t) => [esc(t.date), esc(t.text)]))}`
        + `<h3>Sources</h3><ul class="tips">${r.sources.map((s) => `<li><a href="${esc(s.url)}" target="_blank" rel="noopener noreferrer">${esc(s.title)}</a></li>`).join('')}</ul>` },
    { key: 'thruster', title: 'Cold-gas thruster simulator', blurb: 'Compressed air vents through nozzles: thrust, tank blow-down, hop height. Defaults are illustrative, not Tesla data.',
      fields: [{ n: 'dryMassKg', l: 'Vehicle mass without air (kg)', v: 1800 }, { n: 'tankVolumeL', l: 'Total tank volume (L)', v: 150 }, { n: 'tankPressureBar', l: 'Tank pressure (bar)', v: 500 },
        { n: 'tankTempK', l: 'Gas temperature (K)', v: 300 }, { n: 'nozzleCount', l: 'Nozzles', v: 10 }, { n: 'throatDiameterMm', l: 'Throat diameter each (mm)', v: 8 }, { n: 'ambientPressureBar', l: 'Ambient pressure (bar)', v: 1.013 }],
      render: (r) => kv('Can it lift off?', badge(r.canLiftOff ? 'Yes' : 'No', r.canLiftOff)) + kv('Initial thrust', `${n(r.initialThrustKN)} kN`) + kv('Thrust-to-weight', n(r.thrustToWeight, 2), 'big')
        + kv('Isp', `${n(r.ispSeconds)} s`) + kv('Air stored / used', `${n(r.airMassKg)} / ${n(r.airUsedKg)} kg`) + kv('Time thrust exceeds weight', `${n(r.liftSeconds, 2)} s`)
        + kv('Peak altitude', `${n(r.maxAltitudeM)} m`, 'big') + kv('Airborne time', `${n(r.airTimeS)} s`) + kv('Total impulse', `${n(r.totalImpulseKNs)} kN·s`) + ul(r.insights),
      chart: (r) => ({ type: 'line', data: { labels: r.series.map((p) => p.t), datasets: [
        { label: 'Thrust (kN)', data: r.series.map((p) => p.thrustKN), yAxisID: 'y', pointRadius: 0 },
        { label: 'Tank pressure (bar)', data: r.series.map((p) => p.pressureBar), yAxisID: 'y2', pointRadius: 0 },
        { label: 'Altitude (m)', data: r.series.map((p) => p.altitudeM), yAxisID: 'y3', pointRadius: 0 }] },
        options: { plugins: { legend: { position: 'bottom' } }, scales: { x: { title: { display: true, text: 'Time (s)' } }, y: { position: 'left', title: { display: true, text: 'kN' } },
          y2: { position: 'right', grid: { drawOnChartArea: false }, title: { display: true, text: 'bar' } }, y3: { display: false } } } }) },
    { key: 'hover', title: 'Hover physics: rotor vs jet', blurb: 'Momentum theory shows why eVTOLs use big slow rotors while thrusters are power-hungry.',
      fields: [{ n: 'massKg', l: 'Vehicle mass (kg)', v: 1800 }, { n: 'rotorDiscAreaM2', l: 'Total rotor disc area (m²)', v: 10 }, { n: 'figureOfMerit', l: 'Rotor figure of merit', v: 0.7 },
        { n: 'jetExhaustVelocityMs', l: 'Jet exhaust speed (m/s)', v: 700 }, { n: 'hoverSeconds', l: 'Hover time (s)', v: 60 }, { n: 'batteryWhPerKg', l: 'Battery pack (Wh/kg)', v: 200 }],
      render: (r) => kv('Weight', `${n(r.weightKN)} kN`) + kv('Disc loading', `${n(r.discLoadingNPerM2)} N/m²`) + kv('Rotor hover power', `${n(r.rotorHoverPowerKW)} kW`)
        + kv('Jet hover power', `${n(r.jetHoverPowerKW)} kW`) + kv('Jet needs', `${n(r.powerRatio)}x more power`, 'big') + kv('Rotor energy for hover', `${n(r.rotorEnergyKWh)} kWh`)
        + kv('Battery mass needed', `${n(r.batteryMassKg)} kg`) + kv('Air needed by jets', `${n(r.jetPropellantKg)} kg`, 'big') + ul(r.insights) },
    { key: 'rocket', title: 'Rocket science comparison', blurb: 'Tsiolkovsky equation: what each propulsion type needs for a given delta-v (orbit is about 9,400 m/s with losses).',
      fields: [{ n: 'dryMassKg', l: 'Dry mass (kg)', v: 2000 }, { n: 'deltaVMs', l: 'Delta-v (m/s)', v: 9400 }],
      render: (r) => table(['Propulsion', 'Isp (s)', 'Exhaust (m/s)', 'Mass ratio', 'Propellant (kg)', 'Prop. %', 'Practical', 'Example'],
        r.rows.map((x) => [esc(x.propulsion), n(x.ispS, 0), n(x.exhaustVelocityMs, 0), n(x.massRatio, 2), n(x.propellantKg, 0), n(x.propellantFractionPct), badge(x.practical ? 'Yes' : 'No', x.practical), esc(x.example)]))
        + ul(r.insights),
      chart: (r) => ({ type: 'bar', data: { labels: r.rows.map((x) => x.propulsion), datasets: [{ label: 'Isp (s)', data: r.rows.map((x) => x.ispS) }] },
        options: { indexAxis: 'y', scales: { x: { type: 'logarithmic' } }, plugins: { legend: { display: false } } } }) },
    { key: 'market', title: 'Market landscape', get: true, blurb: 'Tesla versus eVTOL air taxis and roadable flying cars.',
      render: (r) => `<p class="muted">${esc(r.note)}</p>` + table(['Player', 'Category', 'Lift', 'Status (as of ' + esc(r.asOf) + ')'], r.players.map((p) => [`<b>${esc(p.name)}</b>`, esc(p.category), esc(p.lift), esc(p.status)]))
        + `<h3>Thruster car vs eVTOL</h3>` + table(['Trait', 'Tesla thrusters', 'eVTOL'], r.compare.map((c) => [esc(c.trait), esc(c.tesla), esc(c.evtol)])) },
  ];

  const field = (f) => `<label>${f.l}<input name="${f.n}" type="number" value="${f.v}" step="any" required></label>`;

  function drawChart(cfg) {
    if (chart) { chart.destroy(); chart = null; }
    $('#chartWrap').hidden = !cfg || !window.Chart;
    if (cfg && window.Chart) chart = new window.Chart($('#chart'), cfg);
  }
  function show(data) {
    $('#out').hidden = false;
    $('#result').innerHTML = current.render(data);
    drawChart(current.chart ? current.chart(data) : null);
  }
  function fail(err) { $('#out').hidden = false; drawChart(null); $('#result').innerHTML = `<p class="err">${esc(err.message)}</p>`; }

  async function select(m) {
    current = m;
    document.querySelectorAll('#nav button').forEach((b) => b.classList.toggle('active', b.dataset.key === m.key));
    $('#title').textContent = m.title; $('#blurb').textContent = m.blurb;
    $('#out').hidden = true; drawChart(null);
    $('#form').innerHTML = m.fields ? m.fields.map(field).join('') + '<button class="go" type="submit">Run</button>' : '';
    if (m.get) {
      try { const r = await fetch(`api/${m.key}`); if (!r.ok) throw new Error(r.statusText); if (current === m) show(await r.json()); } catch (e) { fail(e); }
    }
  }

  $('#form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const d = new FormData(e.target), body = {};
    for (const f of current.fields) body[f.n] = Number(d.get(f.n));
    try {
      const res = await fetch(`api/${current.key}`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) });
      const json = await res.json();
      if (!res.ok) throw new Error(json.error || res.statusText);
      show(json);
    } catch (err) { fail(err); }
  });

  $('#nav').innerHTML = MODULES.map((m) => `<button type="button" data-key="${m.key}">${m.title}</button>`).join('');
  $('#nav').addEventListener('click', (e) => { const b = e.target.closest('button'); if (b) select(MODULES.find((m) => m.key === b.dataset.key)); });
  select(MODULES[0]);
})();
