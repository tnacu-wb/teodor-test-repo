package uk.co.whitbread.reservation.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.getunleash.Unleash;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.unleash.features.autoconfigure.UnleashProperties;
import uk.co.whitbread.reservation.domain.model.feature.FeatureFlagOverrideResolver;
import uk.co.whitbread.reservation.domain.model.feature.OverrideAwareUnleashWrapper;
import uk.co.whitbread.reservation.domain.model.feature.UnleashWrapper;

class CustomUnleashConfigTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withUserConfiguration(CustomUnleashConfig.class)
          .withBean(Unleash.class, () -> mock(Unleash.class))
          .withBean(Tracer.class, () -> mock(Tracer.class))
          .withBean(UnleashProperties.class, UnleashProperties::new);

  @Test
  void shouldUseStandardWrapperWhenPropertyIsMissing() {
    contextRunner.run(context -> {
      assertThat(context).hasSingleBean(UnleashWrapper.class);
      assertThat(context).doesNotHaveBean(FeatureFlagOverrideResolver.class);
      assertThat(context.getBean(UnleashWrapper.class)).isExactlyInstanceOf(UnleashWrapper.class);
    });
  }

  @Test
  void shouldUseStandardWrapperWhenOverridesAreDisabled() {
    contextRunner
        .withPropertyValues("integration-tests.feature-flag-overrides.enabled=false")
        .run(context -> {
          assertThat(context).hasSingleBean(UnleashWrapper.class);
          assertThat(context).doesNotHaveBean(FeatureFlagOverrideResolver.class);
          assertThat(context.getBean(UnleashWrapper.class))
              .isExactlyInstanceOf(UnleashWrapper.class);
        });
  }

  @Test
  void shouldUseOverrideAwareWrapperWhenOverridesAreEnabled() {
    contextRunner
        .withPropertyValues("integration-tests.feature-flag-overrides.enabled=true")
        .run(context -> {
          assertThat(context).hasSingleBean(UnleashWrapper.class);
          assertThat(context).hasSingleBean(FeatureFlagOverrideResolver.class);
          assertThat(context.getBean(UnleashWrapper.class))
              .isExactlyInstanceOf(OverrideAwareUnleashWrapper.class);
        });
  }
}
