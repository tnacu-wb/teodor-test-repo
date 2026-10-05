package uk.co.whitbread.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.availability-cache")
public class AvailabilityCacheProperties {

  private String host;
  private String availabilityCacheEndpoint;
  private String distrAvailabilityCacheEndpoint;
}
