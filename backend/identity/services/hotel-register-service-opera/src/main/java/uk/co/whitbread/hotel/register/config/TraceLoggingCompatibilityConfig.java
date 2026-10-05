package uk.co.whitbread.hotel.register.config;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.Filter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.micrometer.observation.autoconfigure.ObservationRegistryCustomizer;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import uk.co.whitbread.shared.commons.logging.LoggingConfigurationProperties;
import uk.co.whitbread.shared.commons.logging.debug.DebugFilter;
import uk.co.whitbread.shared.commons.logging.trace.TraceFilter;

@Configuration
@ConditionalOnProperty(prefix = "logging.configuration.debug", name = "enabled", havingValue = "true")
public class TraceLoggingCompatibilityConfig {

    private static final String URL_PATTERN = "*";
    private final LoggingConfigurationProperties properties;

    public TraceLoggingCompatibilityConfig(LoggingConfigurationProperties properties) {
        this.properties = properties;
    }

    @Bean
    ObservationRegistryCustomizer<ObservationRegistry> noSpringSecurityObservations() {
        return registry -> registry.observationConfig()
            .observationPredicate((name, context) -> !name.startsWith("spring.security."));
    }

    @Bean
    public FilterRegistrationBean<DebugFilter> debugFilter() {
        FilterRegistrationBean<DebugFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new DebugFilter(properties));
        registrationBean.addUrlPatterns(URL_PATTERN);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 3);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> tracingFilter(Tracer tracer) {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TraceFilter(tracer));
        registrationBean.addUrlPatterns(URL_PATTERN);
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        return registrationBean;
    }
}
