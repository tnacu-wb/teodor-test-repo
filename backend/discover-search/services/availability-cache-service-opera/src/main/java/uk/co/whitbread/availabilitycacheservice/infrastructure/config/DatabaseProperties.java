package uk.co.whitbread.availabilitycacheservice.infrastructure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@Profile({"!disable-database"})
@ConfigurationProperties(prefix = "sql")
@PropertySource(value = "classpath:sql/queries.properties")
@Data
public class DatabaseProperties {

  private String updateLocationPriceBlock;
}
