package uk.co.whitbread.booking.domain.properties;

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

  private boolean lazyMigrationEnabled;

}