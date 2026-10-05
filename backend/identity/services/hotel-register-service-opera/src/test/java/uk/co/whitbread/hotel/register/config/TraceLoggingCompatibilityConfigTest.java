package uk.co.whitbread.hotel.register.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.ObservationRegistry.ObservationConfig;
import io.micrometer.observation.Observation;
import io.micrometer.tracing.Tracer;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.micrometer.observation.autoconfigure.ObservationRegistryCustomizer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import uk.co.whitbread.shared.commons.logging.LoggingConfigurationProperties;

class TraceLoggingCompatibilityConfigTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            LoggingConfigurationProperties.class,
            TraceLoggingCompatibilityConfig.class))
        .withBean(Tracer.class, () -> Tracer.NOOP);

    @Test
    void shouldLoadCompatibilityBeansWhenDebugLoggingEnabled() {
        contextRunner
            .withPropertyValues("logging.configuration.debug.enabled=true")
            .run(context -> {
                assertThat(context).hasSingleBean(TraceLoggingCompatibilityConfig.class);
                assertThat(context).hasSingleBean(ObservationRegistryCustomizer.class);
                assertThat(context).hasBean("debugFilter");
                assertThat(context).hasBean("tracingFilter");
            });
    }

    @Test
    void shouldFilterSpringSecurityObservations() {
        contextRunner
            .withPropertyValues("logging.configuration.debug.enabled=true")
            .run(context -> {
                ObservationRegistry observationRegistry = ObservationRegistry.create();
                ObservationRegistryCustomizer<ObservationRegistry> customizer =
                    context.getBean(ObservationRegistryCustomizer.class);
                ObservationConfig observationConfig = observationRegistry.observationConfig();
                try {
                    Method isObservationEnabled = observationConfig.getClass().getDeclaredMethod(
                        "isObservationEnabled", String.class, Observation.Context.class);
                    isObservationEnabled.setAccessible(true);

                    customizer.customize(observationRegistry);

                    assertThat(isObservationEnabled.invoke(observationConfig, "spring.security.test", null))
                        .isEqualTo(false);
                    assertThat(isObservationEnabled.invoke(observationConfig, "http.server.requests", null))
                        .isEqualTo(true);
                } catch (ReflectiveOperationException e) {
                    throw new AssertionError(e);
                }
            });
    }

    @Test
    void shouldNotLoadCompatibilityBeansWhenDebugLoggingDisabled() {
        contextRunner
            .withPropertyValues("logging.configuration.debug.enabled=false")
            .run(context -> assertThat(context)
                .doesNotHaveBean(TraceLoggingCompatibilityConfig.class)
                .doesNotHaveBean(ObservationRegistryCustomizer.class));
    }
}
