# AeroCar Lab (Java 21, Tomcat 9, Maven WAR)

Explains Tesla's thruster-assisted Roadster (unveiled 15 Oct 2026), simulates cold-gas thruster physics,
compares rotor vs jet hover power, compares propulsion with rocket science, and maps the flying-car/eVTOL market.

## Architecture (single view)
```
Browser (index.html + app.js + Chart.js)
  | JSON / HTTPS
  v
[ALB / CloudFront: TLS, WAF] -> Tomcat 9 + Java 21 (aerocar.war)
                                 |- SecurityHeadersFilter (CSP, nosniff, no-store on /api)
                                 '- ApiServlet  GET /api/health /tesla /market | POST /api/thruster /hover /rocket
                                       -> ThrusterService (isentropic blow-down + vertical dynamics)
                                       -> HoverService (momentum theory) -> RocketService (Tsiolkovsky)
                                       -> ContentService (curated facts + market, edit per release)
                                 Stateless: no DB, scales horizontally
```
## Build / run
    mvn clean package                  # needs a full JDK 21 (not JRE-only) and Maven 3.8+
    cp target/aerocar.war $CATALINA_HOME/webapps/       # http://localhost:8080/aerocar/
    docker build -t aerocar . && docker run -p 8080:8080 aerocar    # http://localhost:8080/
## Cloud
Image -> ECR -> ECS Fargate behind an ALB; health check GET /api/health. Any Tomcat 9 + Java 21 host works.
## Maintenance
`ContentService` holds news/market facts with an `AS_OF` date; review before each release. Physics inputs are illustrative.
