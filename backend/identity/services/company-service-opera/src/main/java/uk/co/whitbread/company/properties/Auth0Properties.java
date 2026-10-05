package uk.co.whitbread.company.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.auth.properties.ManagementProperties;

@Data
@Primary
@Component
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "auth.management")
public class Auth0Properties extends ManagementProperties {

  private boolean lazyMigrationEnabled;
}
