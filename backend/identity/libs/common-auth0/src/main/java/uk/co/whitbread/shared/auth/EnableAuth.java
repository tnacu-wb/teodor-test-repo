package uk.co.whitbread.shared.auth;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import uk.co.whitbread.shared.auth.config.AuthConfiguration;
import uk.co.whitbread.shared.auth.config.exception.AccessDeniedExceptionHandler;

@Retention(RUNTIME)
@Target(TYPE)
@Import({AuthConfiguration.class, AccessDeniedExceptionHandler.class})
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public @interface EnableAuth {

}
