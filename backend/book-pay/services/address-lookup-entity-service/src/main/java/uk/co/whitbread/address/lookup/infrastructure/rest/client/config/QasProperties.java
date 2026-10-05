package uk.co.whitbread.address.lookup.infrastructure.rest.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "address.lookup.qas")
public class QasProperties {

  private String url;

  private String authToken;

  private String countryLeisureDataSetId;

  private String countryBusinessDataSetId;

  private String layoutLeisure;

  private String layoutBusiness;

  private ConnectionManager connectionManager;
  private int connectionManagerTimeout;
  private int connectionTimeout;
  private int socketTimeout;
  private int defaultKeepAlive;

  @Data
  public static class ConnectionManager {

    private int maxTotal;
    private int defaultMaxPerRoute;
  }

}
