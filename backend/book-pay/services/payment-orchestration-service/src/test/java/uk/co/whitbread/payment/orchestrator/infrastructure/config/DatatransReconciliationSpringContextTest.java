package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class DatatransReconciliationSpringContextTest {

  private static final String PREFIX = "integrations.datatrans.reconciliation.";
  private static final String INITIAL_DELAY_KEY = PREFIX + "initial-delay";
  private static final String POLL_INTERVAL_KEY = PREFIX + "poll-interval";
  private static final String MAX_DURATION_KEY = PREFIX + "max-duration";
  private static final String ENABLED_KEY = PREFIX + "enabled";

  @Test
  void bindsDefaultsFromApplicationYaml() {
    newContextRunner().run(context -> {
      assertThat(context).hasNotFailed();
      DatatransReconciliationProperties properties =
          context.getBean(DatatransReconciliationProperties.class);

      assertThat(properties.getInitialDelay()).isEqualTo(Duration.ofMinutes(2));
      assertThat(properties.getPollInterval()).isEqualTo(Duration.ofSeconds(30));
      assertThat(properties.getMaxDuration()).isEqualTo(Duration.ofMinutes(30));
      assertThat(properties.isEnabled()).isTrue();
    });
  }

  @Test
  void propertyValuesOverrideApplicationYamlDefaults() {
    newContextRunner()
        .withPropertyValues(
            INITIAL_DELAY_KEY + "=4m",
            POLL_INTERVAL_KEY + "=45s",
            MAX_DURATION_KEY + "=1h",
            ENABLED_KEY + "=false")
        .run(context -> {
          assertThat(context).hasNotFailed();
          DatatransReconciliationProperties properties =
              context.getBean(DatatransReconciliationProperties.class);

          assertThat(properties.getInitialDelay()).isEqualTo(Duration.ofMinutes(4));
          assertThat(properties.getPollInterval()).isEqualTo(Duration.ofSeconds(45));
          assertThat(properties.getMaxDuration()).isEqualTo(Duration.ofHours(1));
          assertThat(properties.isEnabled()).isFalse();
        });
  }

  @Test
  void environmentVariablesOverrideApplicationYamlPlaceholders() {
    newContextRunner()
        .withSystemProperties(
            "DATATRANS_RECONCILIATION_INITIAL_DELAY=3m",
            "DATATRANS_RECONCILIATION_POLL_INTERVAL=15s",
            "DATATRANS_RECONCILIATION_MAX_DURATION=20m",
            "DATATRANS_RECONCILIATION_ENABLED=false")
        .run(context -> {
          assertThat(context).hasNotFailed();
          DatatransReconciliationProperties properties =
              context.getBean(DatatransReconciliationProperties.class);

          assertThat(properties.getInitialDelay()).isEqualTo(Duration.ofMinutes(3));
          assertThat(properties.getPollInterval()).isEqualTo(Duration.ofSeconds(15));
          assertThat(properties.getMaxDuration()).isEqualTo(Duration.ofMinutes(20));
          assertThat(properties.isEnabled()).isFalse();
        });
  }

  @ParameterizedTest(name = "startup rejects {0}={1}")
  @MethodSource("invalidDurationProperties")
  void nonPositiveDurationFailsStartupWithFullPropertyKey(String propertyKey, String value) {
    newContextRunner()
        .withPropertyValues(
            INITIAL_DELAY_KEY + "=2m",
            POLL_INTERVAL_KEY + "=30s",
            MAX_DURATION_KEY + "=30m",
            propertyKey + "=" + value)
        .run(context -> {
          assertThat(context).hasFailed();
          assertThat(context.getStartupFailure()).hasStackTraceContaining(propertyKey);
        });
  }

  @Test
  void maxDurationShorterThanInitialDelayFailsStartupWithBothFullPropertyKeys() {
    newContextRunner()
        .withPropertyValues(
            INITIAL_DELAY_KEY + "=2m",
            POLL_INTERVAL_KEY + "=30s",
            MAX_DURATION_KEY + "=1m")
        .run(context -> {
          assertThat(context).hasFailed();
          assertThat(context.getStartupFailure())
              .hasStackTraceContaining(MAX_DURATION_KEY)
              .hasStackTraceContaining(INITIAL_DELAY_KEY);
        });
  }

  private static Stream<Arguments> invalidDurationProperties() {
    return Stream.of(
        Arguments.of(INITIAL_DELAY_KEY, "0s"),
        Arguments.of(INITIAL_DELAY_KEY, "-1s"),
        Arguments.of(POLL_INTERVAL_KEY, "0s"),
        Arguments.of(POLL_INTERVAL_KEY, "-1s"),
        Arguments.of(MAX_DURATION_KEY, "0s"),
        Arguments.of(MAX_DURATION_KEY, "-1s"));
  }

  private static ApplicationContextRunner newContextRunner() {
    return new ApplicationContextRunner()
        .withInitializer(new ConfigDataApplicationContextInitializer())
        .withConfiguration(AutoConfigurations.of(
            ConfigurationPropertiesAutoConfiguration.class,
            ValidationAutoConfiguration.class))
        .withUserConfiguration(DatatransReconciliationProperties.class);
  }
}
