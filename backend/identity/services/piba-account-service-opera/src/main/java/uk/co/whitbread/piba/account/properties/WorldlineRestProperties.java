package uk.co.whitbread.piba.account.properties;

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
public class WorldlineRestProperties {

  private PropertiesByLocation gb;
  private PropertiesByLocation de;
  private String accountInfoEndpoint;
  private String defaultIpAddress;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class PropertiesByLocation {
    private String cultureCode;
    private String username;
    private String password;
    private String url;
    private String companyNumber;
  }
}