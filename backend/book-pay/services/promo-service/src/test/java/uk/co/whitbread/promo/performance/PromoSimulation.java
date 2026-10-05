package uk.co.whitbread.promo.performance;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.CoreDsl;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

public class PromoSimulation extends Simulation {

  //Arrange
  HttpProtocolBuilder httpProtocol = http
      .baseUrl("http://localhost:9137")
      .acceptHeader("application/json")
      .userAgentHeader("Gatling/Performance Test");

  //Scenario
  ScenarioBuilder scn = CoreDsl.scenario("Load Test Sample")
      .exec(http("get-sample")
          .get("/v1/sample/hotels/availabilities")
          .check(status().is(200))
      );

  //Simulation
  public PromoSimulation() {
    this.setUp(
        scn.injectOpen(atOnceUsers(200)).protocols(httpProtocol)
    );
  }
}
