package uk.co.whitbread.shared.commons.logging.config;

import io.micrometer.observation.ObservationPredicate;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
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
@ConditionalOnProperty(value = "logging.configuration.debug.enabled", havingValue = "true")
@RequiredArgsConstructor
public class TraceLoggingConfiguration {

    private static final String URL_PATTERN = "*";
    private final LoggingConfigurationProperties properties;

    @Bean
    ObservationRegistryCustomizer<ObservationRegistry> noSpringSecurityObservations() {
        ObservationPredicate predicate = (name, context) -> !name.startsWith("spring.security.");
        return registry -> registry.observationConfig().observationPredicate(predicate);
    }

    @Bean
    public FilterRegistrationBean<DebugFilter> debugFilter() {
        FilterRegistrationBean<DebugFilter> loggingRegistrationBean = new FilterRegistrationBean<>();
        loggingRegistrationBean.setFilter(new DebugFilter(properties));
        loggingRegistrationBean.addUrlPatterns(URL_PATTERN);
        loggingRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 3);
        return loggingRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> tracingFilter(Tracer tracer) {
        FilterRegistrationBean<Filter> loggingRegistrationBean = new FilterRegistrationBean<>();
        loggingRegistrationBean.setFilter(new TraceFilter(tracer));
        loggingRegistrationBean.addUrlPatterns(URL_PATTERN);
        loggingRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 2);
        return loggingRegistrationBean;
    }
}