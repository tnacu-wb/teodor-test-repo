package uk.co.whitbread.company.employee.properties;

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
    /**
     * Number of seconds for which the ticket is valid before expiration.
     * If set to 0, the ticket will be valid for 432000 seconds (5 days).
     */
    private int forgottenPasswordTtlSec;
}
