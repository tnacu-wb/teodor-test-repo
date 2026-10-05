package uk.co.whitbread.ondemandrefreshservice.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "config.service.opera")
public class OperaProperties {

  private String username;
  private String password;
  private String appKey;
  private String enterpriseId;
  private String clientId;
  private String clientSecret;
  private String scope;
  private String host;
  private String authEndpoint;
  private Boolean isClientCredentialsEnabled = false;
  private Boolean wireTapAccessToken = false;
  private String dailyRatePlansEndpoint;
  private String hotelInventoryEndpoint;
  private String hotelMigrationStatusEndpoint;
  private String searchRateRestrictionCriteriaEndpoint;
  private Long tokenRefreshClockSkew;
}
