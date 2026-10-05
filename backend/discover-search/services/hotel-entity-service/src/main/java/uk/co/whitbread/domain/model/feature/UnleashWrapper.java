package uk.co.whitbread.domain.model.feature;

import io.getunleash.Unleash;
import io.getunleash.UnleashContext;

public record UnleashWrapper<T>(Unleash unleash, T featureFlag) {

  public boolean isEnabled(FeatureFlag.Feature flag) {
    return unleash.isEnabled(flag.getKey(), flag.isFallback());
  }

  public boolean isEnabled(FeatureFlag.Feature flag, String channel) {
    UnleashContext context = UnleashContext.builder()
        .addProperty("channel", channel)
        .build();

    return unleash.isEnabled(flag.getKey(), context, flag.isFallback());
  }
}