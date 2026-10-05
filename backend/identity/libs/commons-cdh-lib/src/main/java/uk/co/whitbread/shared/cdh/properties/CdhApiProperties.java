package uk.co.whitbread.shared.cdh.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "cdh.api")
public class CdhApiProperties implements CustomerDataHubProperties {

  private String host;
}
