package uk.co.whitbread.infrastructure.config.snowdrop;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "config.service.snowdrop")
public class SnowDropProperties {

  private String host;
  private String hotelSearchEndpoint;
}
