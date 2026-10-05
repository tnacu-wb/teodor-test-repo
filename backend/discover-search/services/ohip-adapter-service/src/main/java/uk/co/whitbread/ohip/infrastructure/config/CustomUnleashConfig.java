package uk.co.whitbread.ohip.infrastructure.config;

import io.getunleash.Unleash;
import io.getunleash.UnleashContext;
import io.getunleash.UnleashContextProvider;
import io.micrometer.tracing.BaggageView;
import io.micrometer.tracing.Tracer;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.unleash.features.autoconfigure.UnleashProperties;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlagOverrideResolver;
import uk.co.whitbread.ohip.domain.model.feature.OverrideAwareUnleashWrapper;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.infrastructure.featureflags.BaggageFeatureFlagOverrideResolver;

@Configuration
public class CustomUnleashConfig {

  @Bean
  @ConditionalOnProperty(
      name = "integration-tests.feature-flag-overrides.enabled",
      havingValue = "false",
      matchIfMissing = true)
  UnleashWrapper<FeatureFlag> unleashWrapper(Unleash unleash, FeatureFlag properties) {
    return new UnleashWrapper<>(unleash, properties);
  }

  @Bean
  @ConditionalOnProperty(
      name = "integration-tests.feature-flag-overrides.enabled",
      havingValue = "true")
  FeatureFlagOverrideResolver featureFlagOverrideResolver() {
    return new BaggageFeatureFlagOverrideResolver();
  }

  @Bean
  @ConditionalOnProperty(
      name = "integration-tests.feature-flag-overrides.enabled",
      havingValue = "true")
  UnleashWrapper<FeatureFlag> overrideAwareUnleashWrapper(
      Unleash unleash,
      FeatureFlag properties,
      FeatureFlagOverrideResolver featureFlagOverrideResolver) {
    return new OverrideAwareUnleashWrapper<>(unleash, properties, featureFlagOverrideResolver);
  }

  @ConfigurationProperties(
      prefix = "feature-flags"
  )
  @Bean
  FeatureFlag featureFlag() {
    return new FeatureFlag();
  }

  @Bean
  @ConditionalOnBean(Unleash.class)
  UnleashContextProvider unleashContextProvider(Tracer tracer, UnleashProperties properties) {
    return () -> {

      // The WBD Session ID is taken from baggage
      var sessionId = Optional.ofNullable(tracer.getBaggage("wb-session-id"))
          .map(BaggageView::get)
          .orElse(null);

      // The User ID is taken from Spring Security
      var auth = SecurityContextHolder.getContext().getAuthentication();
      var principal = auth.isAuthenticated() ? auth.getName() : "anonymous";

      return UnleashContext.builder()
          .sessionId(sessionId)
          .environment(properties.getEnvironment())
          .appName(properties.getAppName())
          .userId(principal)
          .now()
          .build();
    };
  }
}
