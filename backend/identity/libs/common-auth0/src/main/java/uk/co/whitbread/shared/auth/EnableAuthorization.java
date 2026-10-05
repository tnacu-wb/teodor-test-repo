package uk.co.whitbread.shared.auth;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import uk.co.whitbread.shared.auth.config.AuthConfiguration;
import uk.co.whitbread.shared.auth.config.LegacyAuthorizationConfiguration;
import uk.co.whitbread.shared.auth.config.exception.AccessDeniedExceptionHandler;

/**
 * Superset of {@link EnableAuth}: enables Spring Security OAuth2
 * <strong>and</strong> registers the ported TokenService + ManagementService beans.
 *
 * <p>Use this in services migrating from {@code commons-auth0}.
 * Services that only need Spring Security JWT validation can keep using {@code @EnableAuth}.
 */
@Retention(RUNTIME)
@Target(TYPE)
@Import({AuthConfiguration.class, AccessDeniedExceptionHandler.class,
        LegacyAuthorizationConfiguration.class})
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public @interface EnableAuthorization {
}
