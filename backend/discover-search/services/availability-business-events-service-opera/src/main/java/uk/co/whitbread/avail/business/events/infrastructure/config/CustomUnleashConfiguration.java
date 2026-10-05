package uk.co.whitbread.avail.business.events.infrastructure.config;

import io.getunleash.Unleash;
import io.getunleash.UnleashContext;
import io.getunleash.UnleashContextProvider;
import io.micrometer.tracing.Tracer;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unleash.features.autoconfigure.UnleashProperties;
import uk.co.whitbread.avail.business.events.domain.model.feature.FeatureFlag;
import uk.co.whitbread.avail.business.events.domain.model.feature.UnleashWrapper;

@Configuration
public class CustomUnleashConfiguration {

  @Bean
  UnleashWrapper<FeatureFlag> unleashWrapper(Unleash unleash, FeatureFlag properties) {
    return new UnleashWrapper<>(unleash, properties);
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
      var sessionId = Optional.ofNullable(tracer.getBaggage("wbd-session-id"))
          .map(s -> s.get())
          .orElse(null);

      return UnleashContext.builder()
          .sessionId(sessionId)
          .environment(properties.getEnvironment())
          .appName(properties.getAppName())
          .now()
          .build();
    };
  }
}
