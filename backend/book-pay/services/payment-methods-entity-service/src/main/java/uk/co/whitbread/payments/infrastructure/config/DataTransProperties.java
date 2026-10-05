package uk.co.whitbread.payments.infrastructure.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import uk.co.whitbread.payments.domain.model.out.DataTransConfig;

@Data
@ConfigurationProperties(prefix = "datatrans")
public class DataTransProperties {

  /**
   * CardOption names that do NOT support Datatrans. Configured via ConfigMap.
   * A CardOption is Datatrans-eligible if its name is absent from this list.
   * To enable a new CardOption for Datatrans, remove it from this list — no code change required.
   */
  private List<String> notSupportedCardOptions = List.of();

  /** Convert to a domain-layer config object for use in port implementations. */
  public DataTransConfig toDomainConfig() {
    return new DataTransConfig(notSupportedCardOptions);
  }
}
