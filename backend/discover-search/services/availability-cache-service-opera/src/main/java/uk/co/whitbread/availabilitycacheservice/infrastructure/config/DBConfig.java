package uk.co.whitbread.availabilitycacheservice.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "db")
@Data
public class DBConfig {

  private int batchSize = 50;
}