package uk.co.whitbread.promo.infrastructure.rest.controller.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "promo.canary")
public class CanaryProperties {

  private final Delay delay = new Delay();
  private final Log log = new Log();
  private Double successRate;

  @Data
  public static class Delay {
    private Integer p50;
    private Integer p95;
    private Integer p99;

    Delay() {
    }
  }

  @Data
  public static class Log {
    private Double warnRate;
    private Double errorRate;

    Log() {
    }
  }
}
