package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.stream.Stream;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class DatatransReconciliationPropertiesTest {

  private static final String PREFIX = "integrations.datatrans.reconciliation.";
  private static final String INITIAL_DELAY_KEY = PREFIX + "initial-delay";
  private static final String POLL_INTERVAL_KEY = PREFIX + "poll-interval";
  private static final String MAX_DURATION_KEY = PREFIX + "max-duration";

  private static Validator validator;

  @BeforeAll
  static void setUpValidator() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
  }

  @Test
  void defaultsMatchApplicationConfiguration() {
    DatatransReconciliationProperties properties = new DatatransReconciliationProperties();

    assertThat(properties.getInitialDelay()).isEqualTo(Duration.ofMinutes(2));
    assertThat(properties.getPollInterval()).isEqualTo(Duration.ofSeconds(30));
    assertThat(properties.getMaxDuration()).isEqualTo(Duration.ofMinutes(30));
    assertThat(properties.isEnabled()).isTrue();
    assertThat(validator.validate(properties)).isEmpty();
  }

  @ParameterizedTest(name = "{0} rejects {1}")
  @MethodSource("nonPositiveDurations")
  void rejectsNonPositiveDurations(String propertyKey, Duration value) {
    DatatransReconciliationProperties properties = new DatatransReconciliationProperties();
    setDuration(properties, propertyKey, value);

    assertThat(validator.validate(properties))
        .anyMatch(violation -> violation.getMessage().contains(propertyKey));
  }

  @Test
  void rejectsMaxDurationShorterThanInitialDelayWithBothFullKeys() {
    DatatransReconciliationProperties properties = new DatatransReconciliationProperties();
    properties.setInitialDelay(Duration.ofMinutes(10));
    properties.setMaxDuration(Duration.ofMinutes(5));

    assertThat(validator.validate(properties))
        .anyMatch(violation -> violation.getMessage().contains(MAX_DURATION_KEY)
            && violation.getMessage().contains(INITIAL_DELAY_KEY));
  }

  @Test
  void bindsValidSpringDurationNotationAndBoolean() {
    newContextRunner()
        .withPropertyValues(
            INITIAL_DELAY_KEY + "=2m",
            POLL_INTERVAL_KEY + "=30s",
            MAX_DURATION_KEY + "=30m",
            PREFIX + "enabled=true")
        .run(context -> {
          assertThat(context).hasNotFailed();
          DatatransReconciliationProperties properties = context
              .getBean(DatatransReconciliationProperties.class);
          assertThat(properties.getInitialDelay()).isEqualTo(Duration.ofMinutes(2));
          assertThat(properties.getPollInterval()).isEqualTo(Duration.ofSeconds(30));
          assertThat(properties.getMaxDuration()).isEqualTo(Duration.ofMinutes(30));
          assertThat(properties.isEnabled()).isTrue();
        });
  }

  @ParameterizedTest(name = "startup rejects {0}")
  @MethodSource("invalidStartupProperties")
  void startupValidationNamesOffendingFullConfigurationKey(
      String propertyKey, String value, String expectedKey) {
    newContextRunner()
        .withPropertyValues(
            INITIAL_DELAY_KEY + "=2m",
            POLL_INTERVAL_KEY + "=30s",
            MAX_DURATION_KEY + "=30m",
            propertyKey + "=" + value)
        .run(context -> {
          assertThat(context).hasFailed();
          assertThat(context.getStartupFailure()).hasStackTraceContaining(expectedKey);
        });
  }

  private static Stream<Arguments> nonPositiveDurations() {
    return Stream.of(
        Arguments.of(INITIAL_DELAY_KEY, Duration.ZERO),
        Arguments.of(INITIAL_DELAY_KEY, Duration.ofSeconds(-1)),
        Arguments.of(POLL_INTERVAL_KEY, Duration.ZERO),
        Arguments.of(POLL_INTERVAL_KEY, Duration.ofSeconds(-1)),
        Arguments.of(MAX_DURATION_KEY, Duration.ZERO),
        Arguments.of(MAX_DURATION_KEY, Duration.ofSeconds(-1)));
  }

  private static Stream<Arguments> invalidStartupProperties() {
    return Stream.of(
        Arguments.of(INITIAL_DELAY_KEY, "0s", INITIAL_DELAY_KEY),
        Arguments.of(POLL_INTERVAL_KEY, "-1s", POLL_INTERVAL_KEY),
        Arguments.of(MAX_DURATION_KEY, "0s", MAX_DURATION_KEY),
        Arguments.of(MAX_DURATION_KEY, "1m", MAX_DURATION_KEY));
  }

  private static void setDuration(
      DatatransReconciliationProperties properties, String propertyKey, Duration value) {
    switch (propertyKey) {
      case INITIAL_DELAY_KEY -> properties.setInitialDelay(value);
      case POLL_INTERVAL_KEY -> properties.setPollInterval(value);
      case MAX_DURATION_KEY -> properties.setMaxDuration(value);
      default -> throw new IllegalArgumentException("Unexpected property key: " + propertyKey);
    }
  }

  private static ApplicationContextRunner newContextRunner() {
    return new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(
            ConfigurationPropertiesAutoConfiguration.class,
            ValidationAutoConfiguration.class))
        .withUserConfiguration(DatatransReconciliationProperties.class);
  }
}
