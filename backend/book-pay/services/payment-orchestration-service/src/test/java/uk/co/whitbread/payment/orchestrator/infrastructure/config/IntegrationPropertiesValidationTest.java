package uk.co.whitbread.payment.orchestrator.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;

/**
 * Startup validation for the properties that carry credentials and downstream addresses.
 *
 * <p>The checked-in default for the Datatrans merchant password is the empty string, so a
 * deployed environment missing {@code DATATRANS_MERCHANT_PASSWORD} used to boot cleanly and
 * only discover it had no credentials when a real customer tried to pay. Failing the context
 * refresh turns a customer-facing failure into a deployment failure, which is the only place
 * it can actually be fixed.
 */
class IntegrationPropertiesValidationTest {

  private static final String BASE_URL_KEY = "integrations.datatrans.base-url";
  private static final String MERCHANT_PASSWORD_KEY = "integrations.datatrans.merchant-password";
  private static final String RESERVATION_HOST_KEY = "integrations.reservation.host";
  private static final String BASKET_HOST_KEY = "integrations.basket.host";
  private static final String PAYMENT_METHOD_HOST_KEY = "integrations.payment-method-service.host";
  private static final String AUTHORIZE_WAIT_KEY =
      "integrations.payment.workflow.authorize-wait";

  @Test
  void datatransPropertiesBindWhenCredentialsArePresent() {
    datatransRunner()
        .withPropertyValues(MERCHANT_PASSWORD_KEY + "=a-password")
        .run(context -> {
          assertThat(context).hasNotFailed();
          DatatransProperties properties = context.getBean(DatatransProperties.class);
          assertThat(properties.getBaseUrl()).isEqualTo("https://api.sandbox.datatrans.com");
          assertThat(properties.getMerchantPassword()).isEqualTo("a-password");
        });
  }

  /** This is the deployment mistake the validation exists to catch. */
  @Test
  void blankMerchantPasswordFailsStartupWithTheFullPropertyKey() {
    datatransRunner()
        .withPropertyValues(MERCHANT_PASSWORD_KEY + "=")
        .run(context -> {
          assertThat(context).hasFailed();
          assertThat(context.getStartupFailure())
              .hasStackTraceContaining(MERCHANT_PASSWORD_KEY);
        });
  }

  @Test
  void blankDatatransBaseUrlFailsStartupWithTheFullPropertyKey() {
    datatransRunner()
        .withPropertyValues(
            MERCHANT_PASSWORD_KEY + "=a-password",
            BASE_URL_KEY + "=")
        .run(context -> {
          assertThat(context).hasFailed();
          assertThat(context.getStartupFailure()).hasStackTraceContaining(BASE_URL_KEY);
        });
  }

  @Test
  void blankReservationHostFailsStartupWithTheFullPropertyKey() {
    runnerFor(ReservationProperties.class)
        .withPropertyValues(RESERVATION_HOST_KEY + "=")
        .run(context -> {
          assertThat(context).hasFailed();
          assertThat(context.getStartupFailure()).hasStackTraceContaining(RESERVATION_HOST_KEY);
        });
  }

  @Test
  void blankBasketHostFailsStartupWithTheFullPropertyKey() {
    runnerFor(BasketProperties.class)
        .withPropertyValues(BASKET_HOST_KEY + "=")
        .run(context -> {
          assertThat(context).hasFailed();
          assertThat(context.getStartupFailure()).hasStackTraceContaining(BASKET_HOST_KEY);
        });
  }

  @Test
  void blankPaymentMethodHostFailsStartupWithTheFullPropertyKey() {
    runnerFor(PaymentMethodProperties.class)
        .withPropertyValues(PAYMENT_METHOD_HOST_KEY + "=")
        .run(context -> {
          assertThat(context).hasFailed();
          assertThat(context.getStartupFailure())
              .hasStackTraceContaining(PAYMENT_METHOD_HOST_KEY);
        });
  }

  /**
   * The hosts checked in to application.yml are non-blank, so the shipped configuration starts.
   */
  @Test
  void checkedInHostDefaultsSatisfyValidation() {
    runnerFor(ReservationProperties.class).run(context -> assertThat(context).hasNotFailed());
    runnerFor(BasketProperties.class).run(context -> assertThat(context).hasNotFailed());
    runnerFor(PaymentMethodProperties.class).run(context -> assertThat(context).hasNotFailed());
  }

  /**
   * The bounded authorize wait is what keeps a slow authorization from being reported as a
   * failure, so a zero or negative value would silently turn every authorization into an
   * immediate "still pending" — a misconfiguration worth failing the deployment over.
   */
  @Test
  void nonPositiveAuthorizeWaitFailsStartupWithTheFullPropertyKey() {
    runnerFor(PaymentWorkflowProperties.class)
        .withPropertyValues(AUTHORIZE_WAIT_KEY + "=0s")
        .run(context -> {
          assertThat(context).hasFailed();
          assertThat(context.getStartupFailure())
              .hasStackTraceContaining(AUTHORIZE_WAIT_KEY);
        });
  }

  @Test
  void checkedInAuthorizeWaitDefaultBinds() {
    runnerFor(PaymentWorkflowProperties.class).run(context -> {
      assertThat(context).hasNotFailed();
      assertThat(context.getBean(PaymentWorkflowProperties.class).getAuthorizeWait())
          .isEqualTo(Duration.ofSeconds(30));
    });
  }

  private static ApplicationContextRunner datatransRunner() {
    return runnerFor(DatatransProperties.class);
  }

  private static ApplicationContextRunner runnerFor(Class<?> propertiesClass) {
    return new ApplicationContextRunner()
        .withInitializer(new ConfigDataApplicationContextInitializer())
        .withConfiguration(AutoConfigurations.of(
            ConfigurationPropertiesAutoConfiguration.class,
            ValidationAutoConfiguration.class))
        .withUserConfiguration(propertiesClass);
  }
}
