package uk.co.whitbread.hotel.account.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.auth.properties.ManagementProperties;

@Data
@Primary
@Component
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "auth.management")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Auth0Properties extends ManagementProperties {

  private String b2cConnection;
  private String b2bConnection;
  private boolean lazyMigrationEnabled;
  /**
   * Number of seconds for which the token is valid before expiration.
   * If not set, the token will be valid for 3600 seconds (1 hour).
   */
  private int resetPasswordTokenTtlSec = 3600;
}
