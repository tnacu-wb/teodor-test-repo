package uk.co.whitbread.payapp.infrastructure.rest.client.worldline.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "worldline.rest")
public class WorldlineProperties {

  private PropertiesByLocation gb;
  private PropertiesByLocation de;
  private String url;
  private String appInitEndpoint;
  private String applicationDetailsEndpoint;
  private String appContactDetailsEndpoint;
  private String appCompanyDetailsEndpoint;
  private String appCancelEndpoint;
  private String appLookupEndpoint;
  private String appCompanyDetailsLookupEndpoint;
  private String defaultIpAddress;
  private String userPreferencesEndpoint;
  private String appCardAddEndpoint;
  private String appCardDeleteEndpoint;
  private String appCardListEndpoint;
  private String appSubmitEndpoint;
  private String appPreCheckEndpoint;
  private String hostedPageAppInitEndpoint;
  private String bankDetailsStatusEndpoint;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PropertiesByLocation {

    private String cultureCode;
    private Integer companyNumber;
    private String username;
    private String password;
  }

}
