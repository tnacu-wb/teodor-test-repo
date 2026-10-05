package uk.co.whitbread.promo.infrastructure.rest.client.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "copy-datasource")
public class CopyDataSourceProperties {

  private String url;
  private String username;
  private String password;
  private String driverClassName;
  private Hikari hikari = new Hikari();

  @Data
  public static class Hikari {

    private int maximumPoolSize = 12;
    private int minimumIdle = 6;
    private long connectionTimeout = 30_000L;
    private long validationTimeout = 5_000L;
    private long idleTimeout = 600_000L;
    private long maxLifetime = 1_800_000L;
    private long leakDetectionThreshold = 60_000L;
    private String connectionTestQuery = "SELECT 1";
  }
}
