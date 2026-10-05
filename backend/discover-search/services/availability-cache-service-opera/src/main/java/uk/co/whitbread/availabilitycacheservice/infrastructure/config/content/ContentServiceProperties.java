package uk.co.whitbread.availabilitycacheservice.infrastructure.config.content;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Data
@EnableAsync
@Configuration
@ConfigurationProperties(prefix = "config.service.content-service")
public class ContentServiceProperties {

  private String host;
  private String hotelInformationEndpoint;
  private String globalConfigEndpoint;
}
