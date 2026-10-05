package uk.co.whitbread.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.dynamics365")
public class Dynamics365Properties {

  private String host;
  private String clientId;
  private String clientSecret;
  private String scope;
  private String createBookingEndpoint;
  private String getTicketEndpoint;

}
