package uk.co.whitbread.ondemandrefreshservice.infrastructure.config;

import io.getunleash.DefaultUnleash;
import io.getunleash.Unleash;
import io.getunleash.UnleashContext;
import io.getunleash.UnleashContextProvider;
import io.getunleash.util.UnleashConfig;
import io.micrometer.tracing.Tracer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.unleash.features.autoconfigure.UnleashProperties;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters.UnleashWrapper;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.FeatureFlag;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(UnleashProperties.class)
public class CustomUnleashConfig {

  @Bean
  @ConditionalOnMissingBean(Tracer.class)
  Tracer tracer() {
    return Tracer.NOOP;
  }

  @Bean(destroyMethod = "shutdown")
  Unleash unleash(UnleashConfig unleashConfig) {
    return new DefaultUnleash(unleashConfig);
  }

  @Bean
  UnleashConfig unleashConfig(UnleashProperties properties, UnleashContextProvider contextProvider) {
    var builder = UnleashConfig.builder()
        .appName(properties.getAppName())
        .environment(properties.getEnvironment())
        .unleashAPI(properties.getApiUrl())
        .unleashContextProvider(contextProvider)
        .fetchTogglesConnectTimeout(properties.getFetchTogglesConnectTimeout())
        .fetchTogglesReadTimeout(properties.getFetchTogglesReadTimeout())
        .fetchTogglesInterval(properties.getFetchTogglesInterval().getSeconds())
        .sendMetricsConnectTimeout(properties.getSendMetricsConnectTimeout())
        .sendMetricsReadTimeout(properties.getSendMetricsReadTimeout())
        .sendMetricsInterval(properties.getSendMetricsInterval().getSeconds())
        .customHttpHeader("Authorization", properties.getApiToken())
        .projectName(properties.getProjectName())
        .synchronousFetchOnInitialisation(properties.isSynchronousFetchOnInitialisation())
        .instanceId(Optional.ofNullable(properties.getInstanceId())
            .filter(instanceId -> !instanceId.isBlank())
            .orElseGet(() -> UUID.randomUUID().toString()));

    if (properties.isDisableMetrics()) {
      builder.disableMetrics();
    }
    return builder.build();
  }

  @Bean
  UnleashWrapper<FeatureFlag> unleashWrapper(Unleash unleash, FeatureFlag properties) {
    return new UnleashWrapper<>(unleash, properties);
  }

  @ConfigurationProperties(prefix = "feature-flags")
  @Bean
  FeatureFlag featureFlag() {
    return new FeatureFlag();
  }

  @Bean
  UnleashContextProvider customUnleashContextProvider(Tracer tracer, UnleashProperties properties) {
    return () -> {
      var sessionId = Optional.ofNullable(tracer.getBaggage("wbd-session-id"))
          .map(baggage -> baggage.get())
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
