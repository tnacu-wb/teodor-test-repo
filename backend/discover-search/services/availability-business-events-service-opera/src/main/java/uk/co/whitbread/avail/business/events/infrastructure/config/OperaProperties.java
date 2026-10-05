package uk.co.whitbread.avail.business.events.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "opera")
public class OperaProperties {

  private Authentication authentication;
  private String username;
  private String password;
  private String appkey;
  private String grantType;
  private String url;
  private String clientId;
  private String clientSecret;
  private boolean enabled;
  private ConnectionManager connectionManager;
  private int connectionManagerTimeout;
  private int connectionTimeout;
  private int socketTimeout;
  private int defaultKeepAlive;
  private int interval;
  private String availabilityHotelLimit;
  private ServiceUrl serviceUrl;
  private int tokenExpiryTimeout;
  private String ocimGrantType;
  private String enterpriseId;
  private String scope;
  private Boolean isClientCredentialsEnabled = false;
  private Long tokenRefreshClockSkew;

  @Data
  public static class ServiceUrl {

    private String oauth;
    private String subscriptionUrl;
    private int subscriptionConnDelay;
  }

  @Data
  public static class ConnectionManager {

    private int maxTotal;
    private int defaultMaxPerRoute;
  }

  @Data
  public static class Authentication {

    private boolean enabled;
    private String username;
    private String password;
  }
}
