package uk.co.whitbread.availabilitycacheservice.infrastructure.config.aem;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "feign-clients.aem")
@Data
@Component
public class AemFeignClientProperties {

  private String url;
  private String country;
  private String language;
  private String resource;
  private String username;
  private String password;
}
