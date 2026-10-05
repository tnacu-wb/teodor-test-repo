package uk.co.whitbread.shared.auth;

import org.springframework.context.annotation.Import;
import uk.co.whitbread.shared.auth.config.AuthorizationConfiguration;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
@Import(AuthorizationConfiguration.class)
public @interface EnableAuthorization {
}
