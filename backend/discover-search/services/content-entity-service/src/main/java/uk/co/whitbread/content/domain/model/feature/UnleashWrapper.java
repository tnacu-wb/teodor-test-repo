package uk.co.whitbread.content.domain.model.feature;

import io.getunleash.Unleash;
import io.getunleash.UnleashContext;

public record UnleashWrapper<T>(Unleash unleash, T featureFlag) {

  public boolean isEnabled(FeatureFlag.Feature flag, UnleashContext context) {
    return unleash.isEnabled(flag.getKey(), context, flag.isFallback());
  }
}
