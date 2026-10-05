package uk.co.whitbread.piba.account.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "cdh")
public class CdhProperties {

  private boolean enableBbDataFetch;
}