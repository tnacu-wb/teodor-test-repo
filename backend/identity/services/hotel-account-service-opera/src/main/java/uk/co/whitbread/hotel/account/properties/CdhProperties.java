package uk.co.whitbread.hotel.account.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.cdh.properties.CustomerDataHubProperties;

@Data
@Component
@ConfigurationProperties(prefix = "cdh")
public class CdhProperties implements CustomerDataHubProperties {

  private boolean lazyMigrationEnabled;
}